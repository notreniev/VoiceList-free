package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import br.com.evknz.vol.free.R;
import model.AllDAO;
import model.ObjetoLista;

import static br.com.evknz.vol.free.R.color;
import static br.com.evknz.vol.free.R.drawable;
import static br.com.evknz.vol.free.R.id;
import static br.com.evknz.vol.free.R.layout;

public class ListaSelectionAdapter extends ArrayAdapter<ObjetoLista> {

    private List<ObjetoLista> objetoLista = null;
	private Context context = null;
    private AllDAO dao = null;
    private Animation animUp = null;

    static class ViewHolder {
        ImageView ivIcon;
		TextView tvlista;
        TextView tvdetalhe;
	}

	private HashMap<Integer, Boolean> mSelection = new HashMap<Integer, Boolean>();

	public ListaSelectionAdapter(Context context, int resource, int textViewResourceId, List<ObjetoLista> objetoLista) {
		super(context, resource, textViewResourceId, objetoLista);
		this.objetoLista = objetoLista;
		this.context = context;
        this.dao = new AllDAO(this.context);
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
			convertView = inflater.inflate(R.layout.lista_adapter, parent, false);

            holder.ivIcon = (ImageView) convertView.findViewById(R.id.ivIcon);
			holder.tvlista = (TextView) convertView.findViewById(R.id.tvLista);
            holder.tvdetalhe = (TextView) convertView.findViewById(R.id.tvDetalhe);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		ObjetoLista objetoLista = this.objetoLista.get(position);
		holder.tvlista.setText(objetoLista.getListName());
        holder.tvdetalhe.setText(dao.getTotalItems(objetoLista.getId()) + " items");

		// let the adapter handle setting up the row views default color
		convertView.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
		if (mSelection.get(this.objetoLista.get(position).getId()) != null) {
			convertView.setBackgroundColor(context.getResources().getColor(
					R.color.custom_background_selected_item));
		}

        if (objetoLista.getArchived() == 1){
            holder.ivIcon.setImageResource(R.drawable.ic_action_archive_dark);
        }else if(objetoLista.getTrashed() == 1){
            holder.ivIcon.setImageResource(R.drawable.ic_action_trash_dark);
        }else{
            holder.ivIcon.setImageResource(R.drawable.ic_action_inbox_dark);
        }

		return convertView;
	}

}
