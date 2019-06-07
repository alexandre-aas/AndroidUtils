package com.s.d.a.a.androidutils;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

public abstract class TransacaoFragmento extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = "androidutils";
    private final Context context;
    private final Fragment fragment;
    private final Transacao transacao;
    private Exception exceptionErro;
    private int progressId;

    public TransacaoFragmento(Fragment fragment, Transacao transacao,int progressId) {
        this.context = fragment.getActivity();
        this.fragment = fragment;
        this.transacao = transacao;
        this.progressId = progressId;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        try {
            // Exibe o ProgressBar antes de iniciar a tranação
            showProgress();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            transacao.executar();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            // Salva o erro e retorna false
            this.exceptionErro = e;
            return false;
        } finally {
            try {
                // Ao final da transação desliga o ProgressBar
                // Sincroniza com a thread de interface
                fragment.getActivity().runOnUiThread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    hideProgress();
                                }
                            });
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        return true;
    }
    @Override
    protected void onPostExecute(Boolean ok) {
        if (ok) {
            // Transação executou com sucesso
            transacao.atualizarView();
        } else {
            // Erro
            Utilitaria.alertDialog(context, "Erro: " + exceptionErro.getMessage());
        }
    }
    // Exibe o ProgressBar
    private void showProgress() {
        View view = fragment.getView();
        if (view != null) {
            ProgressBar progress = view.findViewById(progressId);
            if (progress != null) {
                progress.setVisibility(View.VISIBLE);
            }
        }
    }
    // Desliga o ProgressBar
    private void hideProgress() {
        View view = fragment.getView();
        if (view != null) {
            final ProgressBar progress = view.findViewById(progressId);
            if (progress != null) {
                progress.setVisibility(View.INVISIBLE);
            }
        }
    }
}
