package utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import br.com.evknz.vol.free.R;

public class Dialogos {

	public static void alert(Context context, String title, String mensagem){
		if (title == null || title.length() == 0)
			title = context.getResources().getString(R.string.app_name);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle(title);
		builder.setMessage(mensagem);
		builder.setNegativeButton(R.string.fechar, null);
		builder.show();
	}
	
	public static void alert(Context context, int resTitleId, String mensagem){
		String title = context.getResources().getString(resTitleId);
		alert(context, title, mensagem);
	}
	
	public static void alert(Context context, String mensagem){
		alert(context, null, mensagem);
	}

    public static AlertDialog confirm(final Context context, int res, String title, String message){
        if (context == null)
            throw new RuntimeException("Parâmetro 'context' não pode ser nulo");
        if (res == 0)
            throw new RuntimeException("Parâmetro 'res' (ícone) não pode ser nulo");
        if (title == null)
            throw new RuntimeException("Parâmetro 'title' não pode ser nulo");
        if (message == null){
            throw new RuntimeException("Parâmetro 'mensage' não pode ser nulo");
        }
        return new AlertDialog.Builder(context).setIcon(res).setTitle(title)
                .setMessage(message)
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "Operação realizada com sucesso!", Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("Não", null).show();

    }
}
