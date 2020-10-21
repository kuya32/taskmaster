package com.example.taskmaster;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddTask extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button addTaskPageButton = AddTask.this.findViewById(R.id.addTaskPageButton);
        addTaskPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText itemTitleInput = AddTask.this.findViewById(R.id.taskTitle);
                EditText itemDescriptionInput = AddTask.this.findViewById(R.id.taskDescription);
                String itemTitle = itemTitleInput.getText().toString();
                String itemDescription = itemDescriptionInput.getText().toString();
                System.out.println(String.format("Submitted! New task: %s has been added to the list! Description: %s.", itemTitle, itemDescription));

                Intent goToAllTasks = new Intent(AddTask.this, AllTasks.class);
                goToAllTasks.putExtra("Title", itemTitle);
                goToAllTasks.putExtra("Description", itemDescription);
                Toast toast = Toast.makeText(AddTask.this, "You added a new task", Toast.LENGTH_LONG);
                toast.show();
//                AddTask.this.startActivity(goToAllTasks);
            }
        });
    }
}
