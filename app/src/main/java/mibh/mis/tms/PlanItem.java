package mibh.mis.tms;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class PlanItem extends RecyclerView.Adapter<PlanItem.VersionViewHolder> {

    Context context;
    OnItemClickListener clickListener;
    private ArrayList<HashMap<String, String>> data = new ArrayList<>();

    public PlanItem(Context context, ArrayList<HashMap<String, String>> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.plan_item, viewGroup, false);
        VersionViewHolder viewHolder = new VersionViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(VersionViewHolder versionViewHolder, int i) {
        versionViewHolder.SourceText.setText(data.get(i).get("PLITEMSOURCENAME"));
        versionViewHolder.DestText.setText(data.get(i).get("PLITEMDESTNAME"));
        versionViewHolder.woitem.setText("เส้นทาง No." + data.get(i).get("PLITEMDOCID"));
        String productSt = data.get(i).get("PLITEMPRODUCTPRONAME");
        versionViewHolder.ProductTxt.setText(Html.fromHtml(productSt.replace("ลง", "<font color='red'>ลง</font>").replace("ขึ้น", "<font color='blue'>ขึ้น</font>").replace("\n", "<br>")));
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }


    public class VersionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cardItemLayout;
        TextView SourceText, DestText, woitem, ProductTxt;

        public VersionViewHolder(View itemView) {
            super(itemView);
            cardItemLayout = (CardView) itemView.findViewById(R.id.cardlist_item);
            SourceText = (TextView) itemView.findViewById(R.id.SourceText);
            DestText = (TextView) itemView.findViewById(R.id.DestText);
            woitem = (TextView) itemView.findViewById(R.id.woitem);
            ProductTxt = (TextView) itemView.findViewById(R.id.ProductText);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(v, getPosition());
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

}

