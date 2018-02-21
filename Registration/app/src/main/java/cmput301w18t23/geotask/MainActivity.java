package cmput301w18t23.geotask;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private EditText myName;
    private EditText myPassword;
    //private Button RegisterButton;
   // private Button LoginButton;

    private String userNameValue, passwordValue;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        myName = (EditText) findViewById(R.id.UserName);
        myPassword = (EditText) findViewById(R.id.UserName);
        //RegisterButton = (Button) findViewById(R.id.button_register);
        //LoginButton = (Button) findViewById(R.id.button_login);



        addButton();
    }


    private void addButton(){//handles going to AddSub activity
        Button RegisterButton = (Button)findViewById(R.id.button_register);
        Button LoginButton = (Button)findViewById(R.id.button_register);

        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Register.class));
                finish();
            }
        });

       // LoginButton.setOnClickListener(new View.OnClickListener(){});
    }
}




