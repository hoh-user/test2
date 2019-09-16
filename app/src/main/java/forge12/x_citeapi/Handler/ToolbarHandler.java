package forge12.x_citeapi.Handler;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import forge12.x_citeapi.R;
import forge12.x_citeapi.Views.Dashboard;

/**
 * Handler for the Toolbar on top of each page
 */
public class ToolbarHandler {
    /**
     * Save the instance of the admin handler
     */
    private static ToolbarHandler _sInstance = null;

    /**
     * The current context object
     */
    private AppCompatActivity _context = null;

    /**
     * Singleton
     */
    private ToolbarHandler() {

    }

    /**
     * Allow only one instance of the class to be generated
     */
    public static ToolbarHandler getInstance() {
        if (_sInstance == null) {
            _sInstance = new ToolbarHandler();
        }
        return _sInstance;
    }

    /**
     * Sets the context of the current screen
     *
     * @param context AppCompatActivity
     */
    public void setContext(AppCompatActivity context) {
        this._context = context;
        this.hideDefaultActionBar();
        this.resetSubtitle();
        this.addButtonDashboard();
    }

    /**
     * Used to hide the current action bar because it will be replaced
     * by a custom one.
     */
    private void hideDefaultActionBar() {
        ActionBar bar = this._context.getSupportActionBar();

        if (bar != null) {
            this._context.getSupportActionBar().hide();
        }
    }

    /**
     * Automatically resets the subtitle to ensure its not displayed
     * wrong.
     */
    public void resetSubtitle() {
        if (this._context == null) {
            return;
        }

        View v = this._context.findViewById(R.id.toolbar);

        if (v == null) {
            return;
        }

        TextView txt = v.findViewById(R.id.toolbar_subtitle);

        if (txt == null) {
            return;
        }

        txt.setText("");
        txt.setVisibility(View.GONE);

        ImageView img = v.findViewById(R.id.toolbar_image_spacer);

        if (img == null) {
            return;
        }

        img.setVisibility(View.GONE);
    }

    /**
     * Option to add custom title for the subtitle
     *
     * @param name String the name of the subtilte, e.g. Dashboard
     */
    public void setSubtitle(String name) {
        if (this._context == null) {
            return;
        }

        View v = this._context.findViewById(R.id.toolbar);

        if (v == null) {
            return;
        }

        TextView txt = v.findViewById(R.id.toolbar_subtitle);

        if (txt == null) {
            return;
        }

        txt.setText(name);
        txt.setVisibility(View.VISIBLE);

        ImageView img = v.findViewById(R.id.toolbar_image_spacer);

        if (img == null) {
            return;
        }

        img.setVisibility(View.VISIBLE);
    }

    /**
     * Add the interaction to the Button Dashboard which will be used to return to the dashboard
     */
    private void addButtonDashboard() {
        if (this._context == null) {
            return;
        }

        final AppCompatActivity ctx = this._context;

        View v = ctx.findViewById(R.id.toolbar);

        if (v == null) {
            return;
        }

        ImageButton ButtonDashboard = v.findViewById(R.id.button_toolbar_dashboard);

        ButtonDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ctx, Dashboard.class);
                ctx.finish();
                ctx.startActivity(i);
            }
        });
    }
}
