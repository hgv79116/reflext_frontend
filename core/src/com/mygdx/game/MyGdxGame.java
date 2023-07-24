package com.mygdx.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.mygdx.game.notification.AuthenticatingNotification;
import com.mygdx.game.notification.ConnectingNotification;
import com.mygdx.game.notification.FailedConnectionNotification;
import com.mygdx.game.notification.Notification;
import com.mygdx.game.screens.game_screens.GameInitializedScreen;
import com.mygdx.game.screens.game_screens.InGameScreen;
import com.mygdx.game.screens.game_screens.InQueueScreen;
import com.mygdx.game.screens.MenuScreen;
import networking.connection_action_message.ConnectMessage;
import networking.connection_action_message.DisconnectMessage;
import networking.JsonMessage;
import networking.game_action_message.HitMessage;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MyGdxGame extends Game {
	private final int LOCAL_PORT = 3000;
	private Skin neutralizerSkin;
	// skin management
	private SurviScreen screen;
	// game state management
	private String username;
	// connection management
	private Socket socket;
	private PrintStream socketPrint;
	BlockingQueue<JsonMessage> incomingMessages = null;
	private Thread socketListener;
	private volatile long lastGameStateUpdateTime = -1;
	private volatile JSONObject lastGameState;
	@Override
	public void create() {
		neutralizerSkin = new Skin(Gdx.files.internal("skin/uiskin.json"));
		setMenuScreen();
	}


	@Override
	public void render() {
		if(isConnected()) {
			processMessages();
		}
		super.render();
	}

	@Override
	public void dispose() {
		super.dispose();

		try {
			disconnect();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void connect(String ipAddress, int port) {
		Notification connecting = new ConnectingNotification(ipAddress, port, neutralizerSkin);
		this.screen.putNotification(connecting);
		try {
			initializeConnection(ipAddress, port);
			System.out.println("Client: Connection created");

			// after accepted, send user info
			socketPrint.println(new ConnectMessage(this.username));
		} catch (Exception e) {
			System.out.println("Client: Failed to create a connection, error: " + e);
			Notification conectionError = new FailedConnectionNotification(e.toString(), neutralizerSkin);
			this.screen.putNotification(conectionError);
			return;
		} finally {
			connecting.remove();
		}
	}


	public void exitGame() throws IOException {
		disconnect();
		setMenuScreen();
	}

	public Skin getSkin() {
		return neutralizerSkin;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	private boolean isConnected() {
		return socket != null;
	}

	public long getLastGameStateUpdateTime() {
		return lastGameStateUpdateTime;
	}

	public JSONObject getLastGameState() {
		return lastGameState;
	}

	private void initializeConnection(String ipAddress, int port) throws IOException {
		System.out.println(ipAddress);
		System.out.println(port);

		SocketHints socketHints = new SocketHints();
		socketHints.connectTimeout = 3000;

		this.socket = new Socket(ipAddress, port, null, LOCAL_PORT);
		this.socketPrint = new PrintStream(socket.getOutputStream());
		this.incomingMessages = new LinkedBlockingQueue<>();
		this.socketListener = new Thread(new Runnable() {
//			private final long TIMEOUT = 15000;
			private BufferedReader bufferedReader
					= new BufferedReader(new InputStreamReader(socket.getInputStream()));
			@Override
			public void run() {
				long lastUpdate = System.currentTimeMillis();
				// timeout is not plausible, as there is a period when the server is waiting
				StringBuilder currentLine = new StringBuilder();

				while(!Thread.interrupted()) {
					long currentTime = System.currentTimeMillis();

//					if(currentTime - lastUpdate >= TIMEOUT) {
//						break;
//					}
					 try {
						if (bufferedReader.ready()) {
							int c = bufferedReader.read();
							lastUpdate = System.currentTimeMillis();

							if (c == '\n') {
								String line = currentLine.toString();
								currentLine = new StringBuilder();
								JsonMessage jsonMessage = new JsonMessage(line);

								if(incomingMessages != null) {
									try {
										incomingMessages.put(jsonMessage);
									} catch (InterruptedException e) {
										System.out.println("Putting messsage in queue met with exception " + e);
									}
								}
							}
							else {
								currentLine.append((char)c);
							}
						}
					} catch (IOException e) {
						System.out.println("Reading failed");
						break;
					}
				}
				System.out.println("Interrupted");
				System.out.flush();
			}
		});

		// start listening
		this.socketListener.start();
	}

	public void sendHitMessage(int playerId, int circleId) {
		socketPrint.println(new HitMessage(playerId, circleId));
	}

	private void disconnect() throws IOException {

		this.socketListener.interrupt();

		try {
			this.socketListener.join();
			System.out.println("Disposed listening thread");
		} catch (Exception e) {
			System.out.println("Met with exception" + e);
		}

		this.socketPrint.println(new DisconnectMessage());
		System.out.println(new DisconnectMessage());

		this.socketPrint = null;

		this.incomingMessages = null;

		this.socket.close();
		this.socket = null;
	}

	private void processMessages() {
		assert (incomingMessages != null);
		while(!incomingMessages.isEmpty()) {
			JsonMessage message = incomingMessages.poll();
			try {
				handleMessage(message);
			} catch (Exception e) {
				System.out.println(e);
			}
		}
	}

	private void handleMessage(JsonMessage message) throws IOException {
		String messageCategory = message.getHeader().getString("serverMessageCategory");
		if(messageCategory.equals("CONNECTION_STATUS")) {
			String connectionStatusCategory = message.getHeader().getString("connectionStatusCategory");
			if(connectionStatusCategory.equals("DISCONNECTED")) {
				disconnect();
			}
//			if(connectionStatusCategory.equals("CONNECTED")) {
//				connected();
//			}
			else if(connectionStatusCategory.equals("IN_QUEUE")) {
				movedToQueue();
			}
			else if(connectionStatusCategory.equals("IN_GAME")) {
				movedToGame();
			}
		}
		else if(messageCategory.equals("GAME_STATUS")) {
			String gameStatusMessageCategory = message.getHeader().getString("gameStatusMessageCategory");
			if(gameStatusMessageCategory.equals("GAME_STARTED")) {
				gameStart(message.getBody().getJSONObject("initialGameState"),
						message.getBody().getInt("playerId"));
			}
			else if(gameStatusMessageCategory.equals("GAME_ENDED")) {
				gameEnd();
			}
			else if(gameStatusMessageCategory.equals("GAME_STATE")) {
				gameUpdate(message.getBody().getJSONObject("gameState"));
			}
		}
	}

	private void connected() {
		setInQueueScreen();
	}

	private void movedToQueue() {
		setInQueueScreen();
	}

	private void movedToGame() {
		System.out.println("Currently in game");
	 	setGameInitializedScreen();
	}

	private void gameStart(JSONObject initialGameState, int playerId) {
		gameUpdate(initialGameState);
		System.out.println("Game started!!!");
		setInGameScreen(initialGameState, playerId);
	}

	private void gameEnd() {
		setGameUninitializedScreen();
	}

	private void gameUpdate(JSONObject gameState) {
		this.lastGameStateUpdateTime = System.currentTimeMillis();
		this.lastGameState = gameState;
	}

	private void setGameInitializedScreen() {
		setScreen(new GameInitializedScreen(this));
	}

	private void setInGameScreen(JSONObject initialGameState, int playerId) {
		setScreen(new InGameScreen(this, initialGameState, playerId));
	}

	private void setGameUninitializedScreen() {
//		setScreen(new GameUninitializedScreen(this));
	}

	private void setMenuScreen() {
		setScreen(new MenuScreen(this));
	}

	private void setInQueueScreen() {
		setScreen(new InQueueScreen(this));
	}

	private void setScreen(SurviScreen screen) {
		super.setScreen(screen);
		this.screen = screen;
	}
}
