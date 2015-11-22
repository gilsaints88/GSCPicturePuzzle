package com.gilsaints.picturepuzzle;

import com.gilsaints.picturepuzzle.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;


/**
 * 
 * Create custom Dialog windows for your application
 * Custom dialogs rely on custom layouts wich allow you to 
 * create and use your own look &amp; feel.
 * 
 * Under GPL v3 : http://www.gnu.org/licenses/gpl-3.0.html
 * 
 * @author antoine vianey
 *
 */
public class YouLoseDialog extends Dialog {

    public YouLoseDialog(Context context, int theme) {
        super(context, theme);
    }

    public YouLoseDialog(Context context) {
        super(context);
    }

    /**
     * Helper class for creating a custom dialog
     */
    public static class Builder {

        private Context context;
        private String title;
        private String message;
        private String newGameButtonText;
        private String quitButtonText;
        private String tryAgainButtonText;
        private View contentView;

        private DialogInterface.OnClickListener 
                       newGameButtonClickListener,
                       tryAgainButtonClickListener,
                        quitButtonClickListener;

        public Builder(Context context) {
            this.context = context;
        }

        /**
         * Set the Dialog message from String
         * @param title
         * @return
         */
        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        /**
         * Set the Dialog message from resource
         * @param title
         * @return
         */
        public Builder setMessage(int message) {
            this.message = (String) context.getText(message);
            return this;
        }

        /**
         * Set the Dialog title from resource
         * @param title
         * @return
         */
        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        /**
         * Set the Dialog title from String
         * @param title
         * @return
         */
        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }
        
        /**
         * Set a custom content view for the Dialog.
         * If a message is set, the contentView is not
         * added to the Dialog...
         * @param v
         * @return
         */
        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }

        /**
         * Set the positive button resource and it's listener
         * @param newGameButtonText
         * @param listener
         * @return
         */
        public Builder setNewGameButton(int positiveButtonText,
                DialogInterface.OnClickListener listener) {
            this.newGameButtonText = (String) context
                    .getText(positiveButtonText);
            this.newGameButtonClickListener = listener;
            return this;
        }

        /**
         * Set the positive button text and it's listener
         * @param newGameButtonText
         * @param listener
         * @return
         */
        public Builder setNewGameButton(String positiveButtonText,
                DialogInterface.OnClickListener listener) {
            this.newGameButtonText = positiveButtonText;
            this.newGameButtonClickListener = listener;
            return this;
        }
        
        public Builder setTryAgainButton(int tryAgainButtonText,
                DialogInterface.OnClickListener listener) {
            this.tryAgainButtonText = (String) context
                    .getText(tryAgainButtonText);
            this.tryAgainButtonClickListener = listener;
            return this;
        }

        /**
         * Set the positive button text and it's listener
         * @param newGameButtonText
         * @param listener
         * @return
         */
        public Builder setTryAgainButton(String tryAgainButtonText,
                DialogInterface.OnClickListener listener) {
            this.tryAgainButtonText = tryAgainButtonText;
            this.tryAgainButtonClickListener = listener;
            return this;
        }

        /**
         * Set the negative button resource and it's listener
         * @param quitButtonText
         * @param listener
         * @return
         */
        public Builder setQuitButton(int negativeButtonText,
                DialogInterface.OnClickListener listener) {
            this.quitButtonText = (String) context
                    .getText(negativeButtonText);
            this.quitButtonClickListener = listener;
            return this;
        }

        /**
         * Set the negative button text and it's listener
         * @param quitButtonText
         * @param listener
         * @return
         */
        public Builder setQuitButton(String negativeButtonText,
                DialogInterface.OnClickListener listener) {
            this.quitButtonText = negativeButtonText;
            this.quitButtonClickListener = listener;
            return this;
        }

        /**
         * Create the custom dialog
         */
        public CustomDialog create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final CustomDialog dialog = new CustomDialog(context, 
            		R.style.Dialog);
            View layout = inflater.inflate(R.layout.you_lose, null);
            dialog.addContentView(layout, new LayoutParams(
                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            // set the dialog title
            ((TextView) layout.findViewById(R.id.title)).setText(title);
            // set the confirm button
            if (newGameButtonText != null) {
                ((Button) layout.findViewById(R.id.newGameButton))
                        .setText(newGameButtonText);
                if (newGameButtonClickListener != null) {
                    ((Button) layout.findViewById(R.id.newGameButton))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                	newGameButtonClickListener.onClick(
                                    		dialog, 
                                            DialogInterface.BUTTON1);
                                }
                            });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.newGameButton).setVisibility(
                        View.GONE);
            }
            
            if (tryAgainButtonText != null) {
                ((Button) layout.findViewById(R.id.tryAgainButton))
                        .setText(tryAgainButtonText);
                if (tryAgainButtonClickListener != null) {
                    ((Button) layout.findViewById(R.id.tryAgainButton))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                	tryAgainButtonClickListener.onClick(
                                    		dialog, 
                                            DialogInterface.BUTTON2);
                                }
                            });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.tryAgainButton).setVisibility(
                        View.GONE);
            }
            
            // set the cancel button
            if (quitButtonText != null) {
                ((Button) layout.findViewById(R.id.quitButton))
                        .setText(quitButtonText);
                if (quitButtonClickListener != null) {
                    ((Button) layout.findViewById(R.id.quitButton))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                	quitButtonClickListener.onClick(
                                    		dialog, 
                                            DialogInterface.BUTTON3);
                                }
                            });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.quitButton).setVisibility(
                        View.GONE);
            }
            // set the content message
            if (message != null) {
                ((TextView) layout.findViewById(
                		R.id.message)).setText(message);
            } else if (contentView != null) {
                // if no message set
                // add the contentView to the dialog body
                ((LinearLayout) layout.findViewById(R.id.content))
                        .removeAllViews();
                ((LinearLayout) layout.findViewById(R.id.content))
                        .addView(contentView, 
                                new LayoutParams(
                                        LayoutParams.WRAP_CONTENT, 
                                        LayoutParams.WRAP_CONTENT));
            }
            dialog.setContentView(layout);
            return dialog;
        }

    }

}