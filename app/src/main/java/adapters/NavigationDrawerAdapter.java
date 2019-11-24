package adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.plus.PlusShare;

import java.util.HashMap;
import java.util.Set;

import br.com.evknz.vol.free.R;

public class NavigationDrawerAdapter extends ArrayAdapter<String> {

    private final PlusShare.Builder builder;
    private Context context = null;
    private String[] lista = {};

    static class ViewHolder {
        ImageView ivIcon;
        TextView tvitem;
    }

    private HashMap<Integer, Boolean> mSelection = new HashMap<Integer, Boolean>();

    public NavigationDrawerAdapter(Context context, int resource, int textViewResourceId, String[] lista) {
        super(context, resource, textViewResourceId, lista);
        this.context = context;
        this.lista = lista;
        this.builder = new PlusShare.Builder(this.context);
    }

    public void setNewSelection(int position, boolean value) {
        mSelection.put(position, value);
        notifyDataSetChanged();
    }

    public boolean isPositionChecked(int position) {
        Boolean result = mSelection.get(position) != null;
        return result == null ? false : result;
    }

    public Set<Integer> getCurrentCheckedPosition() {
        return mSelection.keySet();
    }

    public void removeSelection(int position) {
        mSelection.remove(position);
        notifyDataSetChanged();
    }

    public void clearSelection() {
        mSelection = new HashMap<Integer, Boolean>();
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.navigation_drawer_adapter, parent, false);

            holder.ivIcon = (ImageView) convertView.findViewById(R.id.ivIcon);
            holder.tvitem = (TextView) convertView.findViewById(R.id.tvLista);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String item = lista[position];
        holder.tvitem.setText(item);

        // let the adapter handle setting up the row views default color
        if (position == 0){
            holder.ivIcon.setImageResource(R.drawable.ic_action_inbox_black);
        }else if(position == 1){
            holder.ivIcon.setImageResource(R.drawable.ic_action_archive_black);
        }else if(position == 2){
            holder.ivIcon.setImageResource(R.drawable.ic_action_trash_black);
        }else if(position == 4){
            holder.ivIcon.setVisibility(View.GONE);
            holder.tvitem.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
        }

        holder.ivIcon.getLayoutParams().width = 64;
        holder.ivIcon.getLayoutParams().height = 64;

        return convertView;
    }

}
