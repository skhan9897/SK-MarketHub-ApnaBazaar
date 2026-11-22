package com.sk.markethub.ui.support;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.sk.markethub.R;
import com.sk.markethub.api.ApiClient;
import com.sk.markethub.model.Ticket;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SupportActivity extends AppCompatActivity {

    private EditText etSubject, etMessage;
    private Button btnCreateTicket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        etSubject = findViewById(R.id.etSupportSubject);
        etMessage = findViewById(R.id.etSupportMessage);
        btnCreateTicket = findViewById(R.id.btnCreateTicket);

        btnCreateTicket.setOnClickListener(v -> {
            String subject = etSubject.getText().toString().trim();
            String message = etMessage.getText().toString().trim();

            if (subject.isEmpty() || message.isEmpty()) {
                Toast.makeText(this, "Subject and message required", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> body = new HashMap<>();
            body.put("subject", subject);
            body.put("message", message);

            ApiClient.getService().createSupportTicket(body).enqueue(new Callback<Ticket>() {
                @Override
                public void onResponse(Call<Ticket> call, Response<Ticket> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(SupportActivity.this, "Support Ticket Created: " + response.body().getTicketId(), Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(SupportActivity.this, "Failed to create support ticket", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Ticket> call, Throwable t) {
                    Toast.makeText(SupportActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
