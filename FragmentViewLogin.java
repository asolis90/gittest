package com.taxiseguroapp1;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.gson.GsonFactory;
import com.taxiseguroapp1.angelguardendpoint.Angelguardendpoint;
import com.taxiseguroapp1.userendpoint.Userendpoint;

import org.json.JSONException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by Administrador on 24/12/2014.
 */
public class FragmentViewLogin extends Fragment {
    SharedPreferences.Editor editor;
    Spinner emp_spinner, suc_spinner, dept_spinner;
    EditText edusername, edpassword;
    EditText ed_nombre, ed_origen, ed_destino, ed_autorizacion, ed_fecha;
    static String sempresa, sempresaId, ssucursal, snombre, sdept, sorigen, sdestino;
    static String sunidad, sautorizacion, svalor;
    Button btn_iniciar_s, btn_registrarse, btn_login;

    ViewFlipper flipper;
    LoginButton button;
    private UiLifecycleHelper uiHelper;
    FragmentViewLogin loginfragment;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    static FragmentActivity act;
    static View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.login, container,
                false);
        act = LoginActivity.act;
        uiHelper = new UiLifecycleHelper(act, callback);
        uiHelper.onCreate(savedInstanceState);

        flipper = (ViewFlipper) view.findViewById(R.id.logFlipper);
        flipper.setDisplayedChild(0);

        btn_iniciar_s = (Button) view.findViewById(R.id.btn_iniciar_s);
        btn_login = (Button) view.findViewById(R.id.login);
        btn_registrarse = (Button) view.findViewById(R.id.btn_registrarse);
        edusername = (EditText) view.findViewById(R.id.usuario);
        edpassword = (EditText) view.findViewById(R.id.password);
        button = (LoginButton) view.findViewById(R.id.authButton);
        button.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        button.setBackgroundResource(R.drawable.loghome_fb);
        button.setFragment(this);
        button.setReadPermissions(Arrays.asList("public_profile", "email"));
        btn_iniciar_s.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                flipper.setInAnimation(act, R.anim.loghomeflipperin);
                flipper.setOutAnimation(act, R.anim.loghomeflipperout);

                flipper.setInAnimation(act, R.anim.activity_left_in);
                flipper.setDisplayedChild(1);
                //Intent vaIntent = new Intent(act, VoucherConsult.class);
                // act.startActivity(vaIntent);
                //act.overridePendingTransition(R.anim.activity_left_in,
                //       R.anim.shrink);

            }
        });
        btn_registrarse.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                checkBeforeLogin();
            }
        });


        return view;
    }


    private Session.StatusCallback statusCallback =
            new SessionStatusCallback();

    private void onClickLogin() {
        System.out.println("onClickLogin()");
        Session session = Session.getActiveSession();
        if (!session.isOpened() && !session.isClosed()) {
            session.openForRead(new Session.OpenRequest(this)
                    .setPermissions(Arrays.asList("public_profile", "email"))
                    .setCallback(statusCallback));
        } else {
            Session.openActiveSession(act, true, statusCallback);
        }
    }

    private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            // Respond to session state changes, ex: updating the view
        }
    }


    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }


    };


    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i("", "Logged in...");
            // make request to the /me API to get Graph user
            Request.newMeRequest(session, new Request.GraphUserCallback() {

                // callback after Graph API response with user
                // object
                @Override
                public void onCompleted(GraphUser user, Response response) {
                    if (user != null) {
                        // Set view visibility to true
                        try {
                            System.out.println("Graph Inner Json " + user.getInnerJSONObject());
                            String loginuser = user.getInnerJSONObject().getString("email");
                            System.out.println("fblogin user: " + loginuser);
                            String nombre = user.getName();
                            System.out.println("fblogin nombre: " + nombre);
                            String apellido = user.getLastName();
                            System.out.println("fblogin apellido: " + apellido);
                            String fbid = user.getId();
                            System.out.println("fblogin fbid: " + fbid);
                            new loginFBUserTask(act, loginuser, nombre, apellido, fbid).execute();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }).executeAsync();


        } else if (state.isClosed()) {
            Log.i("", "Logged out...");
            session.openForRead(new Session.OpenRequest(this)
                    .setPermissions(Arrays.asList("public_profile", "email"))
                    .setCallback(statusCallback));
        }
    }


//    private static Session openActiveSession(Activity activity, boolean allowLoginUI, Session.StatusCallback statusCallback)
//    {
//        Session.OpenRequest openRequest = new Session.OpenRequest(activity);
//        openRequest.setPermissions(Arrays.asList("user_birthday", "email"));
//        openRequest.setCallback(statusCallback);
//
//        Session session = new Session.Builder(activity).build();
//
//        if(SessionState.CREATED_TOKEN_LOADED.equals(session.getState()) || allowLoginUI)
//        {
//            Session.setActiveSession(session);
//            session.openForRead(openRequest);
//
//            return session;
//        }
//
//        return null;
//    }


    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
//        if(Session.getActiveSession() != null)
//        {
//            Session.getActiveSession().onActivityResult(act, requestCode, resultCode, data);
//        }
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }


    public void clearErros() {
        edusername.setError(null);
        edpassword.setError(null);


    }

    private void checkBeforeLogin() {
        // TODO Auto-generated method stub
        String uname = edusername.getText().toString();
        String pwd = edpassword.getText().toString();
        clearErros();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(uname)) {
            edusername.setError("El campo 'Usuario' es requerido");
            focusView = edusername;
            cancel = true;

        } else if (TextUtils.isEmpty(pwd)) {
            edpassword.setError("El campo 'Contrasena' es requerido");
            focusView = edpassword;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();

        } else {

            InputMethodManager inputMethodManager = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(act.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            if (!isOnline()) {
                Toast.makeText(act, "Error - Sin Conexion a internet. Vuelve a intentar mas tarde..", Toast.LENGTH_SHORT).show();

            } else {
                //  isred = testConnection();

                isred = false;
                isfinished = true;
                do {
                    //nothing ---wait time for thread
                } while (!isfinished);
                if (!isred) {
                    Toast.makeText(act, "Insertando o Logeando..", Toast.LENGTH_SHORT).show();
//                     new insertUserTask(act, uname.toString(), pwd.toString()).execute();
                    //new loginUserTask(act, uname.toString(), pwd.toString()).execute();
//                    SharedPreferences prefs = getSharedPreferences("SHARED_PREFS_FILE", 0);
//                    editor = prefs.edit();
//                    editor.putString("currentUser", uname);
//                    editor.putBoolean("isLogged", true);
//                    editor.commit();
//
//                    Bundle bundle = new Bundle();
//                    // Bundle bundle = new Bundle();
//                    bundle.putString("currentUser", uname);
//
//                    Intent home = new Intent(act, Home.class);
//
//                    home.putExtras(bundle);
//                    act.startActivity(home);
//                    finish();
                } else {
                    //is redirect
                    Toast.makeText(act, "Error - Sin Conexion a internet. Vuelve a intentar mas tarde..", Toast.LENGTH_SHORT).show();
                }


            }

            //   new loginLoad(uname, pwd, a).execute();
        }
    }


    Userendpoint u;
    Dialog sdialog;



    class loginFBUserTask extends AsyncTask<String, String, String> {
        Activity act;
        String susername, spassword, slevel, sestado, sdepartamento, snombre, sapellido, sfbid;
        boolean isvalid, isLoginSuccess, isIndexed, needPassword;
        ProgressDialog dialog;
        Long currDate;
        Boolean hasAngel;

        public loginFBUserTask(Activity a, String username, String nombre, String apellido, String fbid) {
            // TODO Auto-generated constructor stub

            this.act = a;
            this.susername = username;
            this.snombre = nombre;
            this.sapellido = apellido;
            this.sfbid = fbid;


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            dialog = new ProgressDialog(this.act);
            dialog.setMessage("Cargando..");
            dialog.show();
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);

            SimpleDateFormat tdf = new SimpleDateFormat(
                    "dd/MM/yyyy");
            try {
                Date tdf_date = tdf.parse(tdf.format(new Date()));
                System.out.println("date gettime:" + tdf_date.getTime());
                currDate = tdf_date.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        protected String doInBackground(String... urls) {

            System.out.println("dobackground");

            //if Voucher endpoint null use builder
            if (u == null) { // Only do this once
                Userendpoint.Builder builder = new Userendpoint.Builder(
                        AndroidHttp.newCompatibleTransport(),
                        new GsonFactory(), null)
                        .setApplicationName("taxi-seguro-app");
                // end options for devappserver

                u = builder.build();

                try {

                    Log.d("LOGGING", "Logging User..");
                    Log.d("data","user: "+susername+ " fbid: "+sfbid+" nombre: "+ snombre+" apellido: "+ sapellido +" currdate: "+ currDate);


                    TaxiData.isValid = u.loginUsuarioFB(susername, sfbid, snombre, sapellido, currDate).execute();
                    isvalid = TaxiData.isValid.getIsValidated();
                    isIndexed = TaxiData.isValid.getIsIndexed();
                    needPassword = TaxiData.isValid.getNeedsPassword();

                    Log.d("VALIDATING", "isvalid:" + isvalid);
                    isLoginSuccess = true;

                } catch (Exception e) {
                    Log.e("ERROR LOGIN USER", "I got an error:", e);

                    isLoginSuccess = false;
                    return "";

                }

            } else {
                Log.d("BUILDER NOT NULL", "Builder Not Null");

                try {
                    Log.d("Login", "Logging User..");

                    TaxiData.isValid = u.loginUsuarioFB(susername, sfbid, snombre, sapellido, currDate).execute();
                    Log.d("LOGGING", "LOGGING..");
                    isvalid = TaxiData.isValid.getIsValidated();
                    Log.d("LOGING.....", "isvalid:" + isvalid);
                    isLoginSuccess = true;
                    needPassword = TaxiData.isValid.getNeedsPassword();
                    hasAngel = TaxiData.isValid.getHasAngel();


                } catch (Exception e) {

                    Log.e("ERROR LOGIN USER", "I got an error", e);

                    isLoginSuccess = false;
                }

            }

            return "";
        }

        protected void onProgressUpdate(String message) {

            Toast.makeText(act, message, Toast.LENGTH_SHORT).show();

        }

        protected void onPostExecute(String s) {

            if (isLoginSuccess && isvalid) {

                if(hasAngel){

                }else{

                    slevel = TaxiData.isValid.getLevel();
                    sestado = TaxiData.isValid.getEstado();

                    SharedPreferences prefs = act.getSharedPreferences("SHARED_PREFS_FILE", 0);
                    editor = prefs.edit();
                    editor.putString("currentUser", susername);
                    editor.putString("currentUserLevel", slevel);
                    editor.putString("currentUserEstado", sestado);
                    editor.putBoolean("currentUserNeedPassword", needPassword);
                    editor.putBoolean("isLogged", true);
                    editor.commit();

                    final Bundle bundle = new Bundle();
                    bundle.putString("currentUser", susername);
                    bundle.putString("currentUserLevel", slevel);
                    bundle.putBoolean("currentUserNeedPassword", needPassword);


                    sdialog = new Dialog(act);

                    sdialog.requestWindowFeature((int) Window.FEATURE_NO_TITLE);

                    sdialog.setContentView(R.layout.settings_dialog);
                    // dialog.setTitle("Acerca de Nosotros");

                    // there are a lot of settings, for dialog, check them all out!

                    // set up text
                     ed_guardian = (EditText) sdialog.findViewById(R.id.editText_guardian);
                    ed_movil = (EditText) sdialog.findViewById(R.id.editText_movil_g);
                   sw_gps  = (org.jraf.android.backport.switchwidget.Switch) sdialog.findViewById(R.id.gpsSwitch);
                    sw_sonido = (org.jraf.android.backport.switchwidget.Switch) sdialog.findViewById(R.id.recordSwitch);
                    // set up image view




                    Button btnsig = (Button) sdialog.findViewById(R.id.btn_sig);
                    btnsig.setOnClickListener(new View.OnClickListener() {

                        public void onClick(View v) {


                            String guardian = ed_guardian.getText().toString();
                            String movil = ed_movil.getText().toString();
                            Boolean getgps = sw_gps.isChecked();
                            Boolean getsonido = sw_sonido.isChecked();
                            validateData(guardian, movil, getgps, getsonido, susername, bundle);

                        }
                    });

                    sdialog.show();

                }


//                Toast.makeText(act, sdepartamento, Toast.LENGTH_SHORT);
//                Bundle bundle = new Bundle();
//                bundle.putString("currentUser", susername);
//                bundle.putString("currentUserLevel", slevel);
//                bundle.putBoolean("currentUserNeedPassword", needPassword);
//                Intent home = new Intent(act, Home.class);
//
//                dialog.dismiss();
//                home.putExtras(bundle);
//                act.startActivity(home);
//                act.finish();

            } else {

                if (!isvalid) {

                    dialog.dismiss();
                    Toast.makeText(act, "Usuario o Contrasena No Valida, Porfavor Intenta de Nuevo..", Toast.LENGTH_LONG).show();
                    Log.e("loginUserTask()-- onPostExecute()", "Username or password incorrect");

                } else {
                    if (!isIndexed) {
                        Toast.makeText(act, "El usuario no existe..Porfavor intenta con otro usuario..", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(act, "No se pudo Logear al Usuario.. Porfavor intenta mas tarde..", Toast.LENGTH_LONG).show();
                        Log.e("loginUserTask()-- onPostExecute()", "I got an error: ERROR LOGIN USER");
                    }
                    dialog.dismiss();
                }
            }


        }
    }





    Angelguardendpoint ag;
    class setGuardianInfoTask extends AsyncTask<String, String, String> {
        Activity act;
        String susername, spassword, slevel, sestado, sdepartamento, snombre, sapellido, sfbid;
        boolean isvalid, isLoginSuccess, isIndexed, needPassword;
        ProgressDialog dialog;
        Long currDate;
        Boolean hasAngel, getgps, getsonido;
        String sguardian,smovil;
        Bundle bundle;


        public setGuardianInfoTask(Activity a, String username, String guardian, String movil, Boolean getgps, Boolean getsonido, Bundle bundle) {
            // TODO Auto-generated constructor stub

            this.act = a;
            this.susername = username;
            this.sguardian = guardian;
            this.smovil = movil;
            this.getgps = getgps;
            this.getsonido = getsonido;
            this.bundle = bundle;



        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            dialog = new ProgressDialog(this.act);
            dialog.setMessage("Ingresando su Angel Guardian..");
            dialog.show();
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);


        }

        protected String doInBackground(String... urls) {

            System.out.println("dobackground");

            //if Voucher endpoint null use builder
            if (ag == null) { // Only do this once
                Angelguardendpoint.Builder builder = new Angelguardendpoint.Builder(
                        AndroidHttp.newCompatibleTransport(),
                        new GsonFactory(), null)
                        .setApplicationName("taxi-seguro-app");
                // end options for devappserver

                ag = builder.build();

                try {

                    Log.d("LOGGING", "Logging User..");
                    Log.d("data","user: "+susername+ " fbid: "+sfbid+" nombre: "+ snombre+" apellido: "+ sapellido +" currdate: "+ currDate);


                    TaxiData.isValid = ag(susername, sfbid, snombre, sapellido, currDate).execute();
                    isvalid = TaxiData.isValid.getIsValidated();
                    isIndexed = TaxiData.isValid.getIsIndexed();
                    needPassword = TaxiData.isValid.getNeedsPassword();

                    Log.d("VALIDATING", "isvalid:" + isvalid);
                    isLoginSuccess = true;

                } catch (Exception e) {
                    Log.e("ERROR LOGIN USER", "I got an error:", e);

                    isLoginSuccess = false;
                    return "";

                }

            } else {
                Log.d("BUILDER NOT NULL", "Builder Not Null");

                try {
                    Log.d("Login", "Logging User..");

                    TaxiData.isValid = u.loginUsuarioFB(susername, sfbid, snombre, sapellido, currDate).execute();
                    Log.d("LOGGING", "LOGGING..");
                    isvalid = TaxiData.isValid.getIsValidated();
                    Log.d("LOGING.....", "isvalid:" + isvalid);
                    isLoginSuccess = true;
                    needPassword = TaxiData.isValid.getNeedsPassword();
                    hasAngel = TaxiData.isValid.getHasAngel();


                } catch (Exception e) {

                    Log.e("ERROR LOGIN USER", "I got an error", e);

                    isLoginSuccess = false;
                }

            }

            return "";
        }

        protected void onProgressUpdate(String message) {

            Toast.makeText(act, message, Toast.LENGTH_SHORT).show();

        }

        protected void onPostExecute(String s) {

            if (isLoginSuccess && isvalid) {

                if(hasAngel){

                }else{
                    sdialog = new Dialog(act);

                    sdialog.requestWindowFeature((int) Window.FEATURE_NO_TITLE);

                    sdialog.setContentView(R.layout.settings_dialog);
                    // dialog.setTitle("Acerca de Nosotros");

                    // there are a lot of settings, for dialog, check them all out!

                    // set up text
                    ed_guardian = (EditText) sdialog.findViewById(R.id.editText_guardian);
                    ed_movil = (EditText) sdialog.findViewById(R.id.editText_movil_g);
                    sw_gps  = (org.jraf.android.backport.switchwidget.Switch) sdialog.findViewById(R.id.gpsSwitch);
                    sw_sonido = (org.jraf.android.backport.switchwidget.Switch) sdialog.findViewById(R.id.recordSwitch);
                    // set up image view




                    Button btnsig = (Button) sdialog.findViewById(R.id.btn_sig);
                    btnsig.setOnClickListener(new View.OnClickListener() {

                        public void onClick(View v) {


                            String guardian = ed_guardian.getText().toString();
                            String movil = ed_movil.getText().toString();
                            Boolean getgps = sw_gps.isChecked();
                            Boolean getsonido = sw_sonido.isChecked();
                            validateData(guardian, movil, getgps, getsonido, susername, bundle);

                        }
                    });

                    sdialog.show();

                }

                slevel = TaxiData.isValid.getLevel();
                sestado = TaxiData.isValid.getEstado();

                SharedPreferences prefs = act.getSharedPreferences("SHARED_PREFS_FILE", 0);
                editor = prefs.edit();
                editor.putString("currentUser", susername);
                editor.putString("currentUserLevel", slevel);
                editor.putString("currentUserEstado", sestado);
                editor.putBoolean("currentUserNeedPassword", needPassword);
                editor.putBoolean("isLogged", true);
                editor.commit();

                Toast.makeText(act, sdepartamento, Toast.LENGTH_SHORT);
                Bundle bundle = new Bundle();
                bundle.putString("currentUser", susername);
                bundle.putString("currentUserLevel", slevel);
                bundle.putBoolean("currentUserNeedPassword", needPassword);
                Intent home = new Intent(act, Home.class);

                dialog.dismiss();
                home.putExtras(bundle);
                act.startActivity(home);
                act.finish();

            } else {

                if (!isvalid) {

                    dialog.dismiss();
                    Toast.makeText(act, "Usuario o Contrasena No Valida, Porfavor Intenta de Nuevo..", Toast.LENGTH_LONG).show();
                    Log.e("loginUserTask()-- onPostExecute()", "Username or password incorrect");

                } else {
                    if (!isIndexed) {
                        Toast.makeText(act, "El usuario no existe..Porfavor intenta con otro usuario..", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(act, "No se pudo Logear al Usuario.. Porfavor intenta mas tarde..", Toast.LENGTH_LONG).show();
                        Log.e("loginUserTask()-- onPostExecute()", "I got an error: ERROR LOGIN USER");
                    }
                    dialog.dismiss();
                }
            }


        }
    }



    public static EditText ed_guardian, ed_movil;
    public static org.jraf.android.backport.switchwidget.Switch sw_gps, sw_sonido;

    public void validateData(String guardian, String movil, Boolean getgps, Boolean getsonido, String username, Bundle bundle){

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(guardian)) {
            ed_guardian.setError("El campo 'Guardian' es requerido");
            focusView = ed_guardian;
            cancel = true;

        } else if (TextUtils.isEmpty(movil)) {
            ed_movil.setError("El campo 'Movil' es requerido");
            focusView = ed_movil;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();

        } else {

            new setGuardianInfoTask(act, username, guardian, movil, getgps, getsonido, bundle).execute();
            sdialog.dismiss();

        }

    }

    public static boolean isfinished = false;
    public static boolean isconnected;
    public static boolean isred;

    public boolean testConnection() {


        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                String url = "http://www.google.com.com";
                HttpURLConnection connection;
                try {
                    connection = (HttpURLConnection) new URL(url).openConnection();
                    connection.setRequestMethod("HEAD");


                    connection.setInstanceFollowRedirects(false);

                    int resp = connection.getResponseCode();

                    if (resp >= 300) {
                        isred = true;
                        isfinished = true;
                    }

                    System.out.println("finished");
                } catch (IOException exception) {
                    exception.printStackTrace();
                }

            }
        };

        new Thread(runnable).start();
        System.out.println("finished2");
        return isred;
    }


    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }


}
