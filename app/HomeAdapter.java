import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatbox.R;

import java.util.ArrayList;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.VH> {
    Context context;
    ArrayList<user>

    public HomeAdapter(Context context) {
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.recycler_home,parent,false);
        return new HomeAdapter.VH(view);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {

    }

    public class VH extends RecyclerView.ViewHolder{
        TextView txtName,txtMsg,txtTime;

        public VH(@NonNull View itemView) {
            super(itemView);
            txtMsg = itemView.findViewById(R.id.txtMsg);
            txtName = itemView.findViewById(R.id.txtName);
            txtTime = itemView.findViewById(R.id.txtTime);
        }
    }
}
