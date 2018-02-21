package cmput301w18t23.geotask;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends AppCompatActivity {

    private EditText newName;
    //private EditText newAbout;
    //private EditText newAddress;
    //private EditText newPhone;
    private EditText newPassword;
    private EditText newPasswordCheck;
    private Button newUserDataSave;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        newName = (EditText) findViewById(R.id.newName);
        newPassword = (EditText) findViewById(R.id.newPassword);
        newPasswordCheck = (EditText) findViewById(R.id.PasswordCheck);

        newUserDataSave = (Button) findViewById(R.id.newSave);
        newUserDataSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                register_check();
                break;
            }
        if(newUserDataSave ==Null)

            {
                newUserDataSave = new UserDataManager(this);
                newUserDataSave = openDataBase();
            }

        });
    }

    public void register_check() {
        if (isValid()) {
            String userName = newName.getText().toString().trim();
            String userPassword = newPassword.getText().toString().trim();
            String userPasswordCheck = newPasswordCheck.getText().toString().trim();
            //Check existing

            int count = newUserDataSave.findUserByName(userName);
            if (count == 1) {
                Toast.makeText(this, "The name has been used", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!userPassword.equals(userPasswordCheck)) {
                Toast.makeText(this, "different password", Toast.LENGTH_SHORT).show();
                return;
            } else {
                User newUser = new User(userName, userPassword);
                newUserDataSave.openDataBse();
                //
            }
        }
    }

    public boolean isValid() {
        if (newName.getText().toString().trim().equals("")) {
            Toast.makeText(this."Empty name", Toast.LENGTH_SHORT).show();
            return false;
        } else if (newPassword.getText().toString().trim().equals("")) {
            Toast.makeText(this."Empty password", Toast.LENGTH_SHORT).show();
            return false;
        } else if (newPasswordCheck.getText().toString().trim().equals("")) {
            Toast.makeText(this."Empty passwordcheck", Toast.LENGTH_SHORT).show();
            return false;
        }
    }


}
