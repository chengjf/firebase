import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.auth.http.HttpTransportFactory;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import javax.net.ssl.SSLSocketFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created by jeff on 2018/7/18.
 */
public class FirebaseTest {

    public static void main(String[] args) throws IOException, FirebaseAuthException {
        FileInputStream serviceAccount =
                new FileInputStream("C:\\Users\\jeff\\Desktop\\fir-ba85b-firebase-adminsdk-5t8nk-cd5be467f2.json");

        SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();

        NetHttpTransport.Builder builder = new NetHttpTransport.Builder();
        builder.setProxy(new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", 1333)));
        builder.setSslSocketFactory(sslSocketFactory);


        final HttpTransport httpTransport = builder.build();

        HttpTransportFactory httpTransportFactory=()->httpTransport;

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount,httpTransportFactory))
                .setDatabaseUrl("https://fir-ba85b.firebaseio.com")
                .setHttpTransport(httpTransport)
                .build();

        FirebaseApp.initializeApp(options);

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("restricted_access/secret_document");
        DatabaseReference usersRef = ref.child("users");

        Map<String, User> users = new HashMap();
        users.put("alanisawesome", new User("June 23, 1912", "Alan Turing"));
        users.put("gracehop", new User("December 9, 1906", "Grace Hopper"));

        usersRef.setValueAsync(users);



        System.out.println("All done");
        CountDownLatch latch = new CountDownLatch(1);
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
