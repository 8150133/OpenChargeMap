package pt.ipp.estg.openchargesmap.API;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import pt.ipp.estg.openchargesmap.PostosActivity;
import pt.ipp.estg.openchargesmap.R;

public class PostosAdapter extends RecyclerView.Adapter<PostosAdapter.ViewHolder> {
    private Context mContext;
    private List<PostosCarregamento> mListPostos;

    public PostosAdapter(Context mContext, List<PostosCarregamento> mListPostos) {
        this.mContext = mContext;
        this.mListPostos = mListPostos;
    }

    @NonNull
    @Override
    public PostosAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View postosView = inflater.inflate(R.layout.item_posto, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(postosView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PostosAdapter.ViewHolder viewHolder, int i) {
        final PostosCarregamento apiPC = mListPostos.get(i);

        TextView textPostos = viewHolder.nPostos;
        TextView textMorada = viewHolder.morada;
        TextView textCidade = viewHolder.cidade;
        TextView textLatitude = viewHolder.latitude;
        TextView textLongitude = viewHolder.longitude;


        textPostos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PostosActivity.class);
                String nPosto = String.valueOf(apiPC.getAdressInfo().getTitle());
                String morada = String.valueOf(apiPC.getAdressInfo().getAdressLine1());
                String cidade = String.valueOf(apiPC.getAdressInfo().getTown());
                String latitude = String.valueOf(apiPC.getAdressInfo().getLatitude());
                String longitude = String.valueOf(apiPC.getAdressInfo().getLongitude());

                intent.putExtra("posto",nPosto);
                intent.putExtra("morada",morada);
                intent.putExtra("cidade",cidade);
                intent.putExtra("latitude",latitude);
                intent.putExtra("longitude",longitude);

                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mListPostos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nPostos;
        public TextView morada;
        public TextView cidade;
        public TextView latitude;
        public TextView longitude;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nPostos = (TextView) itemView.findViewById(R.id.nome_posto);
            morada = (TextView) itemView.findViewById(R.id.morada);
            cidade = (TextView) itemView.findViewById(R.id.cidade);
            latitude = (TextView) itemView.findViewById(R.id.latitude);
            longitude = (TextView) itemView.findViewById(R.id.longitude);
        }
    }
}
