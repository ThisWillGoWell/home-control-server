package system.spotify;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;
import com.wrapper.spotify.Api;
import com.wrapper.spotify.exceptions.WebApiException;
import com.wrapper.spotify.methods.CurrentUserRequest;
import com.wrapper.spotify.methods.authentication.AuthorizationCodeGrantRequest;
import com.wrapper.spotify.methods.authentication.ClientCredentialsGrantRequest;
import com.wrapper.spotify.models.AuthorizationCodeCredentials;
import com.wrapper.spotify.models.ClientCredentials;
import com.wrapper.spotify.models.Image;
import com.wrapper.spotify.models.User;

import controller.Engine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import parcel.Parcel;
import parcel.StateValue;
import parcel.SystemException;
import system.SystemParent;


import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Willi on 1/8/2017.
 *
 */
public class Spotify extends SystemParent{

    private String clientID = "538b286fcbb7421bb2c474395fe2e2e1";
    private String clientSecret = "936c3e62b7a34bb88c677710b4194c2f";
    private String redirectURI = "localhost:8080/spotifyRD";

    final private String usernameCode = "AQBLFK5iUJzv8M73tltjj2ICQ4XagSPyJHGzjrngcQ-3UxqrN9JCxMvUzAsjGNXcGYbcZmGMULVSuXE9CH9fgPW6o6V5E5lRIEmtTbIQdawRHiGeKHGAhkL3FWMQFpk3zd0aZkA1fzSUh10TUI9bADEENhay0QOV5l8ss7i3OIJaK91v-eirtadLleZp-cL-KGcJ5Oue1y5z8HuNW1TdSca4S--ssI8f_8esmiUV2EysXLDk-k9GxHA";
    private long accessCodeExirationTime = 0;
    private Api api;
    private Parcel state;


    CloseableHttpClient client;

    private static Parcel DEAFULT_SPOTIFY_STATE(){
        Parcel p = new Parcel();
        p.put("userCode", new StateValue("", StateValue.READ_WRITE_PRIVLAGE));
        return p;
    };

    public Spotify(String name, Engine e){
        super(name, e);
        client = HttpClientBuilder.create().build();
        api= Api.builder().clientId(clientID).clientSecret(clientSecret).redirectURI(redirectURI).build();
        state = DEAFULT_SPOTIFY_STATE();
    }

    @Override
    public void run() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sendAuthCodeRequset();
        super.run();
    }

    private void sendAuthCodeRequset(){
        System.out.println(retreiveAuthorizationCode());
        try {
            java.awt.Desktop.getDesktop().browse(URI.create(retreiveAuthorizationCode()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void printUserInfo(){
        final CurrentUserRequest request = api.getMe().build();

        try {
            final User user = request.get();

            System.out.println("Display systemIdentifier: " + user.getDisplayName());
            System.out.println("Email: " + user.getEmail());

            System.out.println("Images:");
            for (Image image : user.getImages()) {
                System.out.println(image.getUrl());
            }

            System.out.println("This account is a " + user.getProduct() + " account");
        } catch (Exception e) {
            System.out.println("Something went wrong!" + e.getMessage());
        }
    }
    private void setAccessCode() throws SystemException {

      /* Application details necessary to get an access token */

    /* Make a token request. Asynchronous requests are made with the .getAsync method and synchronous requests
    * are made with the .get method. This holds for all type of requests. */
    final SettableFuture<AuthorizationCodeCredentials> authorizationCodeCredentialsFuture = api.authorizationCodeGrant(state.getString("userCode")).build().getAsync();

        /* Add callbacks to handle success and failure */
        Futures.addCallback(authorizationCodeCredentialsFuture, new FutureCallback<AuthorizationCodeCredentials>() {
            @Override
            public void onSuccess(AuthorizationCodeCredentials authorizationCodeCredentials) {
            /* The tokens were retrieved successfully! */
            System.out.println("Successfully retrieved an access token! " + authorizationCodeCredentials.getAccessToken());
            System.out.println("The access token expires in " + authorizationCodeCredentials.getExpiresIn() + " seconds");
            System.out.println("Luckily, I can refresh it using this refresh token! " +     authorizationCodeCredentials.getRefreshToken());

            /* Set the access token and refresh token so that they are used whenever needed */
            api.setAccessToken(authorizationCodeCredentials.getAccessToken());
            api.setRefreshToken(authorizationCodeCredentials.getRefreshToken());
            }

            @Override
            public void onFailure(Throwable throwable) {
            /* Let's say that the client id is invalid, or the code has been used more than once,
            * the request will fail. Why it fails is written in the throwable's message. */

            }
        });
    }

    //will retrive an authorization code for sign in to approve app on account
    private String retreiveAuthorizationCode(){
        final List<String> scopes = Arrays.asList("user-read-private","user-read-email");
        final String state = "someExpectedStateString";
        String authorizeURL = api.createAuthorizeURL(scopes, state);
        return authorizeURL;

    }


    public static void main(String args[]){

        Spotify s = new Spotify("testSpot", new Engine());
        s.printUserInfo();
        System.out.println();
    }

    @Override
    public Parcel process(Parcel p) {
        try {
            switch (p.getString("op")){
                case "get":
                    switch (p.getString("what")) {
                        case "state":
                            return Parcel.RESPONSE_PARCEL(state);
                        default:
                            if(state.contains(p.getString("what"))) {
                                StateValue sp = state.getStateParcel(p.getString("what"));
                                if (sp.canRead()) {
                                    return Parcel.RESPONSE_PARCEL(sp.getValue());
                                }
                                throw SystemException.ACCESS_DENIED(p);
                            }
                            throw SystemException.WHAT_NOT_SUPPORTED(p);
                    }
                case "set":
                    switch (p.getString("what")) {
                        default:
                            StateValue sp = state.getStateParcel(p.getString("what"));
                            if (sp.canWrite()) {
                                sp.update(p.get("to"));
                                return Parcel.RESPONSE_PARCEL(sp.getValue());
                            }
                            throw SystemException.ACCESS_DENIED(p);
                    }
                case "login":
                    setAccessCode();
                    return Parcel.RESPONSE_PARCEL("");
                default:
                    throw SystemException.OP_NOT_SUPPORTED(p);
            }
        } catch (SystemException e) {
            return Parcel.RESPONSE_PARCEL_ERROR(e);
        }
    }
}
