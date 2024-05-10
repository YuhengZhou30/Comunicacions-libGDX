package com.yuheng30.comunicacions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.net.HttpStatus;
public class MainMenuScreen implements Screen {
	final Comunicacions game;
	OrthographicCamera camera;
	Texture background;
	Stage stage;
	Skin skin;


	public MainMenuScreen(final Comunicacions game) {
		this.game = game;
		background = new Texture(Gdx.files.internal("backgroundMenu.jpg"));
		skin = new Skin(Gdx.files.internal("uiskin.json"));
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 450, 800);
		stage = new Stage(new FitViewport(450, 800, camera));
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void show() {


		TextButton enviar = new TextButton("Enviar", skin);
		enviar.setPosition(125, 200);
		enviar.setHeight(75);
		enviar.setWidth(200);
		TextArea nameField = new TextArea("Nickname", skin);
		nameField.setHeight(50);
		nameField.setWidth(222);
		nameField.setPosition(110, 340);

		enviar.addListener(new ClickListener() {
			String url = "https://randomfox.ca/floof/";
			@Override
			public void clicked(InputEvent event, float x, float y) {
				sendHttpRequest(url, new HttpResponseListener() {
					@Override
					public void onResponseReceived(String response) {
						// Maneja la respuesta recibida
						System.out.println("Response: " + response);
						nameField.setText(response);
					}

					@Override
					public void onError(String errorMessage) {
						// Maneja errores
						System.out.println("Error: " + errorMessage);
					}
				});
			}
		});


		stage.addActor(enviar);
		stage.addActor(nameField);
	}

	public static void sendHttpRequest(final String url, final HttpResponseListener listener) {
		HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
		Net.HttpRequest httpRequest = requestBuilder.newRequest().method(Net.HttpMethods.GET).url(url).build();

		Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
			@Override
			public void handleHttpResponse(Net.HttpResponse httpResponse) {
				HttpStatus status = httpResponse.getStatus();
				int statusCode = status.getStatusCode();
				String result = httpResponse.getResultAsString();

				// Imprime el código de estado en la salida estándar
				System.out.println("Status Code: " + statusCode);

				// Llama al método callback con la respuesta recibida
				if (listener != null) {
					listener.onResponseReceived(result);
				}
			}

			@Override
			public void failed(Throwable t) {
				// Maneja el fallo de la solicitud HTTP
				t.printStackTrace();
				if (listener != null) {
					listener.onError(t.getMessage());
				}
			}

			@Override
			public void cancelled() {
				// Maneja la cancelación de la solicitud HTTP
				System.out.println("Request cancelled");
				if (listener != null) {
					listener.onError("Request cancelled");
				}
			}
		});
	}
	public interface HttpResponseListener {
		void onResponseReceived(String response);
		void onError(String errorMessage);
	}
	@Override
	public void render(float delta) {
		ScreenUtils.clear(0, 0, 0.2f, 1);

		camera.update();
		game.batch.setProjectionMatrix(camera.combined);

		game.batch.begin();
		game.batch.draw(background, 0, 0);
		game.batch.end();

		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
		stage.draw();
	}



	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
		background.dispose();
	}
}
