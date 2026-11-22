package be.wolvero.captionlab;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.content.res.ColorStateList;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class MainActivity extends Activity {
    private static final int PICK_VIDEO = 14;
    private static final int BG = Color.rgb(11, 15, 23);
    private static final int CARD = Color.rgb(23, 29, 41);
    private static final int INNER = Color.rgb(29, 36, 49);
    private static final int WHITE = Color.rgb(242, 244, 251);
    private static final int MUTED = Color.rgb(156, 167, 187);
    private static final int PURPLE = Color.rgb(183, 158, 248);
    private static final int STROKE = Color.rgb(48, 58, 73);
    private static final int GOOD = Color.rgb(114, 224, 176);
    private static final long MAX_SIZE = GeminiClient.MAX_FILE_BYTES;

    private final ExecutorService tasks = Executors.newSingleThreadExecutor();
    private File selectedVideo;
    private String selectedMime = "video/mp4";
    private String selectedName = "";
    private boolean working;

    private LinearLayout content, results, previewBox;
    private LinearLayout introSection, uploadSection, guidanceSection, resultHeader, footerSection;
    private View connectionRow;
    private Button keyButton, chooseButton;
    private TextView resultVideoLabel;
    private boolean showingResults = false;
    private TextView videoName, sizeLabel, progressLabel, keyState, tavilyState, connectionLabel;
    private View setupNotice;
    private ProgressBar progressBar;
    private Button analyzeButton;
    private EditText gameInput, creatorNoteInput;
    private LinearLayout contextExtra;
    private Button contextToggle;
    private ImageView previewImage;
    private ScrollView scroll;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);
        buildScreen();
    }

    private int dp(float value) { return Math.round(getResources().getDisplayMetrics().density * value); }
    private LinearLayout.LayoutParams lp(int width, int height) {
        return new LinearLayout.LayoutParams(width < 0 ? width : dp(width), height < 0 ? height : dp(height));
    }
    private GradientDrawable shape(int color, float radius, int stroke) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(radius));
        if (stroke != 0) d.setStroke(dp(1), stroke);
        return d;
    }
    private void pad(View view, int h, int v) { view.setPadding(dp(h), dp(v), dp(h), dp(v)); }
    private TextView text(String s, float sp, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextColor(color);
        t.setTextSize(sp);
        t.setFontFeatureSettings("kern");
        t.setTypeface(Typeface.create("sans-serif", bold ? Typeface.BOLD : Typeface.NORMAL));
        t.setIncludeFontPadding(false);
        t.setLineSpacing(dp(3), 1f);
        return t;
    }
    private void addText(LinearLayout parent, String s, int size, int color, boolean bold, int top) {
        TextView t = text(s, size, color, bold);
        LinearLayout.LayoutParams p = lp(-1, -2);
        p.topMargin = dp(top);
        parent.addView(t, p);
    }
    private LinearLayout vertical() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        return l;
    }
    private LinearLayout horizontal() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.HORIZONTAL);
        l.setGravity(Gravity.CENTER_VERTICAL);
        return l;
    }
    private Button action(String label, boolean primary, View.OnClickListener listener) {
        Button b = new Button(this);
        b.setAllCaps(false);
        b.setText(label);
        b.setTextSize(14);
        b.setLetterSpacing(0.02f);
        b.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
        b.setTextColor(primary ? BG : WHITE);
        b.setBackground(shape(primary ? PURPLE : INNER, 13, primary ? 0 : STROKE));
        b.setMinHeight(dp(52));
        b.setPadding(dp(14), dp(8), dp(14), dp(8));
        b.setOnClickListener(listener);
        return b;
    }
    private LinearLayout card() {
        LinearLayout l = vertical();
        l.setBackground(shape(CARD, 19, STROKE));
        pad(l, 18, 18);
        return l;
    }
    private void addSpacer(LinearLayout parent, int size) {
        View v = new View(this);
        parent.addView(v, lp(1, size));
    }

    private GradientDrawable gradient(int from, int to, int radius) {
        GradientDrawable d = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                new int[]{from, to});
        d.setCornerRadius(dp(radius));
        return d;
    }
    private TextView chip(String title) {
        TextView t = text(title, 10, MUTED, true);
        t.setGravity(Gravity.CENTER);
        t.setBackground(shape(INNER, 11, STROKE));
        t.setPadding(dp(11), dp(9), dp(11), dp(9));
        return t;
    }
    private void buildScreen() {
        scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(BG);
        scroll.setClipToPadding(true);
        scroll.setOnApplyWindowInsetsListener((view, insets) -> {
            view.setPadding(0, insets.getSystemWindowInsetTop(), 0, insets.getSystemWindowInsetBottom());
            return insets;
        });
        content = vertical();
        content.setPadding(dp(20), dp(25), dp(20), dp(42));
        scroll.addView(content, new ScrollView.LayoutParams(-1, -2));
        setContentView(scroll);

        LinearLayout header = horizontal();
        TextView logo = text("CL", 23, Color.WHITE, true);
        logo.setGravity(Gravity.CENTER);
        logo.setBackground(gradient(Color.rgb(146, 97, 250), Color.rgb(89, 72, 195), 15));
        header.addView(logo, lp(54, 54));
        logo.setContentDescription("Caption Lab. Lang indrukken om je API-sleutels te wijzigen.");
        logo.setOnLongClickListener(v -> { showSetupDialog(); return true; });
        LinearLayout wordmark = vertical();
        LinearLayout.LayoutParams wm = new LinearLayout.LayoutParams(0, -2, 1);
        wm.leftMargin = dp(13);
        wordmark.addView(text("CAPTION", 18, WHITE, true));
        TextView subtitle = text("L A B", 11, MUTED, true);
        LinearLayout.LayoutParams su = lp(-1, -2);
        su.topMargin = dp(4);
        wordmark.addView(subtitle, su);
        header.addView(wordmark, wm);
        keyButton = action("⚙", false, v -> showSetupDialog());
        keyButton.setContentDescription("API-sleutels instellen");
        keyButton.setTextSize(23);
        keyButton.setPadding(0, 0, 0, 0);
        header.addView(keyButton, lp(50, 50));
        content.addView(header, lp(-1, -2));

        introSection = vertical();
        content.addView(introSection, lp(-1, -2));
        addSpacer(introSection, 30);
        TextView eyebrow = text("YOUR GAMEPLAY. YOUR STORY.", 11, GOOD, true);
        introSection.addView(eyebrow, lp(-1, -2));
        addText(introSection, "Every great clip\nhas a story.", 33, WHITE, true, 11);
        addText(introSection, "Upload je gameplay. Wij zoeken zelf de juiste toon én de juiste woorden.", 14, MUTED, false, 11);
        addSpacer(introSection, 19);

        LinearLayout tags = horizontal();
        TextView a = chip("◉  VIDEO + AUDIO");
        TextView b = chip("⌕  GAME RESEARCH");
        LinearLayout.LayoutParams tag1 = new LinearLayout.LayoutParams(0, -2, 1f);
        tag1.rightMargin = dp(9);
        tags.addView(a, tag1);
        tags.addView(b, new LinearLayout.LayoutParams(0, -2, 1f));
        introSection.addView(tags, lp(-1, -2));
        addSpacer(introSection, 21);

        uploadSection = vertical();
        LinearLayout hero = uploadSection;
        hero.setBackground(gradient(Color.rgb(38, 31, 72), Color.rgb(19, 26, 41), 23));
        hero.setPadding(dp(19), dp(21), dp(19), dp(21));
        content.addView(hero, lp(-1, -2));
        LinearLayout uploadTitle = horizontal();
        TextView upIcon = text("↥", 31, PURPLE, true);
        upIcon.setGravity(Gravity.CENTER);
        upIcon.setBackground(shape(Color.rgb(53, 44, 93), 16, Color.rgb(83, 65, 132)));
        uploadTitle.addView(upIcon, lp(53, 53));
        LinearLayout uploadWords = vertical();
        LinearLayout.LayoutParams uw = new LinearLayout.LayoutParams(0, -2, 1);
        uw.leftMargin = dp(13);
        uploadWords.addView(text("YOUR NEXT MOMENT", 11, PURPLE, true));
        TextView upl = text("Drop your clip in.", 19, WHITE, true);
        LinearLayout.LayoutParams ut = lp(-1, -2);
        ut.topMargin = dp(4);
        uploadWords.addView(upl, ut);
        uploadTitle.addView(uploadWords, uw);
        hero.addView(uploadTitle, lp(-1, -2));

        previewBox = vertical();
        previewBox.setGravity(Gravity.CENTER);
        previewBox.setBackground(shape(Color.rgb(21, 24, 41), 16, Color.rgb(85, 69, 127)));
        LinearLayout.LayoutParams pp = lp(-1, -2);
        pp.topMargin = dp(21);
        hero.addView(previewBox, pp);
        pad(previewBox, 12, 17);
        previewImage = new ImageView(this);
        previewImage.setAdjustViewBounds(true);
        previewImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        previewImage.setVisibility(View.GONE);
        previewBox.addView(previewImage, lp(-1, 170));
        videoName = text("Your next great clip starts here", 14, WHITE, true);
        videoName.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams nameLp = lp(-1, -2);
        nameLp.topMargin = dp(10);
        previewBox.addView(videoName, nameLp);
        sizeLabel = text("MP4, MOV of WebM  •  tot 80 MB", 12, MUTED, false);
        sizeLabel.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams sizeLp = lp(-1, -2);
        sizeLp.topMargin = dp(7);
        previewBox.addView(sizeLabel, sizeLp);
        addSpacer(hero, 15);
        chooseButton = action("＋  Selecteer video", false, v -> pickVideo());
        hero.addView(chooseButton, lp(-1, 50));

        addSpacer(content, 15);
        guidanceSection = card();
        LinearLayout guidance = guidanceSection;
        content.addView(guidance, lp(-1, -2));
        addText(guidance, "GAME (OPTIONEEL)", 11, PURPLE, true, 0);
        addText(guidance, "De app herkent je game zelf. Weet je dat hij zich vergist? Dan kun je hier de titel invullen.", 12, MUTED, false, 9);
        gameInput = new EditText(this);
        gameInput.setSingleLine(true);
        gameInput.setTextColor(WHITE);
        gameInput.setHintTextColor(MUTED);
        gameInput.setTextSize(15);
        gameInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        gameInput.setHint("Automatisch herkennen");
        gameInput.setBackground(shape(INNER, 12, STROKE));
        gameInput.setPadding(dp(14), dp(11), dp(14), dp(11));
        LinearLayout.LayoutParams glp = lp(-1, 52);
        glp.topMargin = dp(13);
        guidance.addView(gameInput, glp);

        addSpacer(guidance, 11);
        contextToggle = action("＋  Extra context toevoegen (optioneel)", false, v -> {
            boolean open = contextExtra.getVisibility() == View.VISIBLE;
            contextExtra.setVisibility(open ? View.GONE : View.VISIBLE);
            contextToggle.setText(open ? "＋  Extra context toevoegen (optioneel)" :
                    "−  Extra context verbergen");
        });
        guidance.addView(contextToggle, lp(-1, 48));
        contextExtra = vertical();
        contextExtra.setVisibility(View.GONE);
        guidance.addView(contextExtra, lp(-1, -2));
        addText(contextExtra, "Alleen als je zelf iets wilt meegeven", 13, WHITE, true, 15);
        creatorNoteInput = new EditText(this);
        creatorNoteInput.setTextColor(WHITE);
        creatorNoteInput.setHintTextColor(MUTED);
        creatorNoteInput.setTextSize(14);
        creatorNoteInput.setGravity(Gravity.TOP | Gravity.START);
        creatorNoteInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        creatorNoteInput.setHint("Bijvoorbeeld: niet verklappen wat er in de verte kruipt.");
        creatorNoteInput.setBackground(shape(INNER, 12, STROKE));
        creatorNoteInput.setPadding(dp(14), dp(13), dp(14), dp(13));
        LinearLayout.LayoutParams clp = lp(-1, 95);
        clp.topMargin = dp(11);
        contextExtra.addView(creatorNoteInput, clp);

        addSpacer(content, 15);
        analyzeButton = action("✨  CREATE MY CAPTIONS   →", true, v -> beginAnalysis());
        analyzeButton.setTextSize(15);
        analyzeButton.setBackground(gradient(Color.rgb(198, 172, 253), Color.rgb(154, 119, 233), 16));
        content.addView(analyzeButton, lp(-1, 59));
        setButtonReady();
        addSpacer(content, 15);

        LinearLayout statusRow = horizontal();
        connectionRow = statusRow;
        TextView dot = text("●", 12, GOOD, true);
        statusRow.addView(dot, lp(17, -2));
        connectionLabel = text("AI-verbinding instellen", 12, MUTED, false);
        statusRow.addView(connectionLabel, new LinearLayout.LayoutParams(0, -2, 1f));
        content.addView(statusRow, lp(-1, -2));

        setupNotice = card();
        LinearLayout notice = (LinearLayout) setupNotice;
        LinearLayout.LayoutParams np = lp(-1, -2);
        np.topMargin = dp(15);
        content.addView(notice, np);
        addText(notice, "KLAAR VOOR JE EERSTE CLIP?", 11, PURPLE, true, 0);
        addText(notice, "Verbind eenmalig je gratis Gemini- en Tavily-sleutels. Daarna kun je rechtstreeks aan de slag.", 13, WHITE, false, 9);
        addSpacer(notice, 13);
        notice.addView(action("Sleutels instellen  →", false, v -> showSetupDialog()), lp(-1, 49));

        progressBar = new ProgressBar(this);
        progressBar.setIndeterminateTintList(ColorStateList.valueOf(PURPLE));
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.GONE);
        LinearLayout.LayoutParams barp = lp(44, 44);
        barp.gravity = Gravity.CENTER_HORIZONTAL;
        barp.topMargin = dp(20);
        content.addView(progressBar, barp);
        progressLabel = text("", 13, PURPLE, true);
        progressLabel.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams statp = lp(-1, -2);
        statp.topMargin = dp(11);
        content.addView(progressLabel, statp);

        resultHeader = vertical();
        LinearLayout.LayoutParams rhp = lp(-1, -2);
        rhp.topMargin = dp(28);
        content.addView(resultHeader, rhp);
        addText(resultHeader, "YOUR RESULTS", 12, PURPLE, true, 0);
        resultVideoLabel = text("", 14, MUTED, false);
        LinearLayout.LayoutParams rv = lp(-1, -2);
        rv.topMargin = dp(6);
        resultHeader.addView(resultVideoLabel, rv);
        addSpacer(resultHeader, 15);
        resultHeader.addView(action("＋  Nieuwe video kiezen", false, v -> pickVideo()), lp(-1, 48));

        results = vertical();
        LinearLayout.LayoutParams rp = lp(-1, -2);
        rp.topMargin = dp(8);
        content.addView(results, rp);

        keyState = text("", 12, MUTED, false);
        tavilyState = text("", 12, MUTED, false);
        refreshKeyState();
        refreshTavilyState();

        footerSection = vertical();
        LinearLayout footer = footerSection;
        LinearLayout.LayoutParams fp = lp(-1, -2);
        fp.topMargin = dp(35);
        footer.setGravity(Gravity.CENTER);
        footer.addView(text("MADE FOR THE MOMENTS WORTH SHARING", 10, MUTED, true));
        TextView foot = text("CAPTION LAB", 9, PURPLE, true);
        LinearLayout.LayoutParams footp = lp(-2, -2);
        footp.topMargin = dp(7);
        footer.addView(foot, footp);
        content.addView(footer, fp);
        updateUiStage();
        if (setupNotice.getVisibility() == View.VISIBLE) {
            scroll.post(this::showSetupDialog);
        }
    }

    private void updateUiStage() {
        boolean hasVideo = selectedVideo != null && selectedVideo.exists();
        if (introSection == null || resultHeader == null) return;
        introSection.setVisibility(hasVideo ? View.GONE : View.VISIBLE);
        uploadSection.setVisibility(showingResults ? View.GONE : View.VISIBLE);
        guidanceSection.setVisibility(hasVideo && !showingResults ? View.VISIBLE : View.GONE);
        analyzeButton.setVisibility(hasVideo && !showingResults ? View.VISIBLE : View.GONE);
        resultHeader.setVisibility(showingResults ? View.VISIBLE : View.GONE);
        results.setVisibility(showingResults ? View.VISIBLE : View.GONE);
        footerSection.setVisibility(hasVideo ? View.GONE : View.VISIBLE);
        chooseButton.setText(hasVideo ? "↻  Andere video kiezen" : "＋  Selecteer video");
    }
    private void setButtonReady() {
        if (analyzeButton == null) return;
        boolean ready = !working && selectedVideo != null && selectedVideo.exists();
        analyzeButton.setEnabled(ready);
        analyzeButton.setAlpha(ready ? 1f : 0.48f);
    }
    private void refreshKeyState() {
        refreshSetupStatus();
    }
    private void refreshTavilyState() {
        refreshSetupStatus();
    }
    private void refreshSetupStatus() {
        if (connectionLabel == null || setupNotice == null) return;
        boolean gemini = false, tavily = false;
        try { gemini = !SecureKeys.load(this).isEmpty(); } catch (Exception ignored) { }
        try { tavily = !SecureKeys.loadTavily(this).isEmpty(); } catch (Exception ignored) { }
        // Once configured, don't keep a permanent settings icon or setup block on screen.
        keyButton.setVisibility(gemini && tavily ? View.GONE : View.VISIBLE);
        connectionRow.setVisibility(gemini && tavily ? View.GONE : View.VISIBLE);
        if (gemini && tavily) {
            connectionLabel.setText("AI ready");
            connectionLabel.setTextColor(GOOD);
            setupNotice.setVisibility(View.GONE);
        } else if (gemini) {
            connectionLabel.setText("Gemini verbonden  •  online broncontrole nog instellen");
            connectionLabel.setTextColor(MUTED);
            setupNotice.setVisibility(View.VISIBLE);
        } else {
            connectionLabel.setText("Eenmalige configuratie nodig");
            connectionLabel.setTextColor(MUTED);
            setupNotice.setVisibility(View.VISIBLE);
        }
    }
    private EditText secretField(String hint) {
        EditText input = new EditText(this);
        input.setSingleLine(true);
        input.setTextSize(14);
        input.setTextColor(WHITE);
        input.setHintTextColor(MUTED);
        input.setHint(hint);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setBackground(shape(INNER, 12, STROKE));
        input.setPadding(dp(14), dp(11), dp(14), dp(11));
        return input;
    }
    private void showSetupDialog() {
        LinearLayout layout = vertical();
        layout.setPadding(dp(21), dp(13), dp(21), dp(6));
        TextView heading = text("One-time setup.", 24, WHITE, true);
        layout.addView(heading, lp(-1, -2));
        TextView help = text("Plak hieronder je twee sleutels. Ze blijven versleuteld op je telefoon en worden nooit aan GitHub toegevoegd.", 13, MUTED, false);
        LinearLayout.LayoutParams hp = lp(-1, -2);
        hp.topMargin = dp(8);
        layout.addView(help, hp);

        addText(layout, "GEMINI  •  VIDEO + CAPTIONS", 11, PURPLE, true, 22);
        EditText geminiInput = secretField("AIza...");
        LinearLayout.LayoutParams gi = lp(-1, 49);
        gi.topMargin = dp(9);
        layout.addView(geminiInput, gi);
        addText(layout, "TAVILY  •  ONLINE GAME RESEARCH", 11, PURPLE, true, 20);
        EditText tavilyInput = secretField("tvly-...");
        LinearLayout.LayoutParams ti = lp(-1, 49);
        ti.topMargin = dp(9);
        layout.addView(tavilyInput, ti);

        addText(layout, "Al ingesteld? Laat een veld leeg om de bewaarde sleutel te behouden.", 11, MUTED, false, 14);
        addText(layout, "De video wordt voor de analyse naar Gemini verstuurd.", 11, MUTED, false, 12);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(layout)
                .setNegativeButton("Annuleren", null)
                .setPositiveButton("Bewaar & start", null)
                .create();
        dialog.setOnShowListener(ignored -> {
            if (dialog.getWindow() != null)
                dialog.getWindow().setBackgroundDrawable(shape(CARD, 20, STROKE));
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(PURPLE);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(MUTED);
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String g = geminiInput.getText().toString().trim();
                String t = tavilyInput.getText().toString().trim();
                if (!g.isEmpty() && (g.length() < 12 || g.contains(" "))) {
                    geminiInput.setError("Controleer je Gemini-sleutel."); return;
                }
                if (!t.isEmpty() && (!t.startsWith("tvly-") || t.length() < 15)) {
                    tavilyInput.setError("Controleer je Tavily-sleutel."); return;
                }
                try {
                    if (g.isEmpty() && SecureKeys.load(this).isEmpty()) {
                        geminiInput.setError("Gemini-sleutel is nodig."); return;
                    }
                    if (t.isEmpty() && SecureKeys.loadTavily(this).isEmpty()) {
                        tavilyInput.setError("Tavily-sleutel is nodig voor online onderzoek."); return;
                    }
                    if (!g.isEmpty()) SecureKeys.save(this, g);
                    if (!t.isEmpty()) SecureKeys.saveTavily(this, t);
                    refreshSetupStatus();
                    dialog.dismiss();
                    Toast.makeText(this, "Alles klaar. Kies je eerste video!", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(this, "Kon de sleutels niet veilig opslaan.", Toast.LENGTH_LONG).show();
                }
            });
        });
        dialog.show();
    }
    private void showKeyDialog() { showSetupDialog(); }

    private void pickVideo() {
        if (working) return;
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("video/*");
        startActivityForResult(intent, PICK_VIDEO);
    }
    @Override protected void onActivityResult(int request, int result, Intent data) {
        super.onActivityResult(request, result, data);
        if (request != PICK_VIDEO || result != RESULT_OK || data == null || data.getData() == null) return;
        final Uri selected = data.getData();
        final String name = displayName(selected);
        final String mime = normalizeMime(getContentResolver().getType(selected), name);
        if (mime == null) {
            Toast.makeText(this, "Dit videoformaat wordt niet ondersteund. Probeer MP4 of WebM.", Toast.LENGTH_LONG).show();
            return;
        }
        working = true;
        setButtonReady();
        setProgress("Video voorbereiden …", true);
        tasks.execute(() -> {
            File tmp = null;
            try {
                tmp = new File(getCacheDir(), "selected_" + System.currentTimeMillis() + ".video");
                long bytes = 0;
                try (InputStream in = getContentResolver().openInputStream(selected);
                     FileOutputStream out = new FileOutputStream(tmp)) {
                    if (in == null) throw new Exception("Geen toegang tot de video.");
                    byte[] buffer = new byte[64 * 1024];
                    int n;
                    while ((n = in.read(buffer)) != -1) {
                        bytes += n;
                        if (bytes > MAX_SIZE) throw new Exception("Deze video is groter dan 80 MB.");
                        out.write(buffer, 0, n);
                    }
                }
                if (bytes == 0) throw new Exception("De video is leeg.");
                final File ready = tmp;
                final long fileSize = bytes;
                runOnUiThread(() -> {
                    if (selectedVideo != null) selectedVideo.delete();
                    selectedVideo = ready;
                    selectedName = name;
                    selectedMime = mime;
                    showingResults = false;
                    // Do not allow hidden context from another clip to affect the next analysis.
                    gameInput.setText("");
                    creatorNoteInput.setText("");
                    contextExtra.setVisibility(View.GONE);
                    contextToggle.setText("＋  Extra context toevoegen (optioneel)");
                    videoName.setText(name);
                    sizeLabel.setText(String.format(Locale.getDefault(), "%.1f MB · klaar om te analyseren", fileSize / 1024.0 / 1024.0));
                    showThumbnail(ready);
                    working = false;
                    setButtonReady();
                    setProgress("", false);
                    results.removeAllViews();
                    updateUiStage();
                    scroll.post(() -> scroll.smoothScrollTo(0, 0));
                });
            } catch (Exception e) {
                if (tmp != null) tmp.delete();
                final String message = e.getMessage();
                runOnUiThread(() -> {
                    working = false;
                    setButtonReady();
                    setProgress("", false);
                    new AlertDialog.Builder(this).setTitle("Video niet beschikbaar")
                            .setMessage(message).setPositiveButton("OK", null).show();
                });
            }
        });
    }
    private void showThumbnail(File video) {
        try (MediaMetadataRetriever retriever = new MediaMetadataRetriever()) {
            retriever.setDataSource(video.getAbsolutePath());
            Bitmap bmp = retriever.getFrameAtTime(1_000_000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
            if (bmp != null) {
                int targetW = 540;
                int targetH = Math.max(100, Math.round((float) bmp.getHeight() * targetW / bmp.getWidth()));
                Bitmap thumb = Bitmap.createScaledBitmap(bmp, targetW, targetH, true);
                if (bmp != thumb) bmp.recycle();
                previewImage.setImageBitmap(thumb);
                previewImage.setVisibility(View.VISIBLE);
            }
        } catch (Exception ignored) { previewImage.setVisibility(View.GONE); }
    }
    private String displayName(Uri uri) {
        try (Cursor c = getContentResolver().query(uri, new String[]{OpenableColumns.DISPLAY_NAME}, null, null, null)) {
            if (c != null && c.moveToFirst()) return c.getString(0);
        } catch (Exception ignored) { }
        return "gameplay-video.mp4";
    }
    private String normalizeMime(String mime, String name) {
        String lower = name.toLowerCase(Locale.ROOT);
        if ("video/mp4".equals(mime) || lower.endsWith(".mp4") || lower.endsWith(".m4v")) return "video/mp4";
        if ("video/webm".equals(mime) || lower.endsWith(".webm")) return "video/webm";
        if ("video/quicktime".equals(mime) || lower.endsWith(".mov")) return "video/quicktime";
        if ("video/x-matroska".equals(mime) || lower.endsWith(".mkv")) return "video/x-matroska";
        return null;
    }
    private void setProgress(String text, boolean active) {
        progressBar.setVisibility(active ? View.VISIBLE : View.GONE);
        progressLabel.setText(text);
    }
    private void beginAnalysis() {
        if (working || selectedVideo == null) return;
        final String apiKey;
        final String tavilyKey;
        try { apiKey = SecureKeys.load(this); }
        catch (Exception e) { showKeyDialog(); return; }
        if (apiKey.isEmpty()) { showKeyDialog(); return; }
        String optionalKey;
        try { optionalKey = SecureKeys.loadTavily(this); }
        catch (Exception ignored) { optionalKey = ""; }
        tavilyKey = optionalKey;
        final String gameHint = gameInput == null ? "" : gameInput.getText().toString().trim();
        // User convention: the first underscore separates the game from purely personal
        // file-organization text. Never give the suffix to any AI request.
        final String filenameGameHint = FilenameGameHint.extract(selectedName);
        final String creatorNote = creatorNoteInput == null ? "" : creatorNoteInput.getText().toString().trim();
        working = true;
        setButtonReady();
        results.removeAllViews();
        setProgress("Upload starten …", true);
        tasks.execute(() -> {
            try {
                GeminiClient.Result result = GeminiClient.analyze(selectedVideo, selectedMime, apiKey, tavilyKey,
                        gameHint, filenameGameHint, creatorNote,
                        status -> runOnUiThread(() -> setProgress(status, true)));
                runOnUiThread(() -> {
                    setProgress("", false);
                    working = false;
                    setButtonReady();
                    showResult(result);
                });
            } catch (Exception error) {
                runOnUiThread(() -> {
                    setProgress("", false);
                    working = false;
                    setButtonReady();
                    new AlertDialog.Builder(this).setTitle("Analyse niet gelukt")
                            .setMessage(error.getMessage() == null ? "Onbekende fout." : error.getMessage())
                            .setPositiveButton("OK", null).show();
                });
            }
        });
    }
    private void showResult(GeminiClient.Result data) {
        results.removeAllViews();
        resultVideoLabel.setText(data.game + "  •  " + selectedName);
        addText(results, String.format(Locale.getDefault(), "%02d CAPTIONS OM UIT TE KIEZEN", data.captions.size()), 12, PURPLE, true, 15);
        addSpacer(results, 13);
        int i = 0;
        for (GeminiClient.Caption caption : data.captions) {
            final int index = ++i;
            LinearLayout item = vertical();
            item.setBackground(shape(CARD, 17, STROKE));
            pad(item, 17, 17);
            LinearLayout.LayoutParams itemp = lp(-1, -2);
            itemp.bottomMargin = dp(12);
            results.addView(item, itemp);
            LinearLayout top = horizontal();
            top.addView(text(String.format(Locale.getDefault(), "%02d  %s",
                    index, caption.style.toUpperCase(Locale.ROOT)),
                    11, PURPLE, true), new LinearLayout.LayoutParams(0, -2, 1f));
            item.addView(top, lp(-1, -2));
            addText(item, caption.text, 17, WHITE, true, 13);
            addSpacer(item, 13);
            Button copy = action("⧉  Copy caption", false, v -> {
                ClipboardManager clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                clip.setPrimaryClip(ClipData.newPlainText("Caption Lab caption", caption.text));
                Toast.makeText(this, "Caption gekopieerd!", Toast.LENGTH_SHORT).show();
            });
            item.addView(copy, lp(-1, 44));
        }

        // Keep research transparent, but let the captions be the primary screen.
        LinearLayout explanation = card();
        LinearLayout.LayoutParams ep = lp(-1, -2);
        ep.topMargin = dp(12);
        results.addView(explanation, ep);
        addText(explanation, "WAAROM DEZE CAPTIONS?", 11, PURPLE, true, 0);
        TextView detailsTitle = text("Videoanalyse en bronnen bekijken  ＋", 14, WHITE, true);
        LinearLayout.LayoutParams dtp = lp(-1, -2);
        dtp.topMargin = dp(12);
        explanation.addView(detailsTitle, dtp);

        LinearLayout detailContent = vertical();
        detailContent.setVisibility(View.GONE);
        explanation.addView(detailContent, lp(-1, -2));
        detailsTitle.setOnClickListener(v -> {
            boolean open = detailContent.getVisibility() == View.VISIBLE;
            detailContent.setVisibility(open ? View.GONE : View.VISIBLE);
            detailsTitle.setText(open ? "Videoanalyse en bronnen bekijken  ＋" :
                    "Videoanalyse en bronnen verbergen  −");
        });
        addText(detailContent, "GAME", 11, PURPLE, true, 18);
        addText(detailContent, data.game, 18, WHITE, true, 7);
        if ("provided".equals(data.confidence)) {
            addText(detailContent, "Door jou opgegeven", 12, GOOD, false, 6);
        } else {
            addText(detailContent, "Herkenning: " + translateConfidence(data.confidence),
                    12, MUTED, false, 6);
        }
        if (!data.scene.isEmpty()) {
            addText(detailContent, "WAT ER GEBEURT", 11, PURPLE, true, 18);
            addText(detailContent, data.scene, 14, WHITE, false, 7);
        }
        if (!data.storyArc.isEmpty()) {
            addText(detailContent, "VERLOOP EN SFEER", 11, PURPLE, true, 18);
            addText(detailContent, data.storyArc, 14, WHITE, false, 7);
        }
        if (!data.audioCues.isEmpty()) {
            addText(detailContent, "GELUID", 11, PURPLE, true, 18);
            addText(detailContent, data.audioCues, 14, WHITE, false, 7);
        }
        if (!data.sources.isEmpty() || !data.chapter.isEmpty()) {
            addText(detailContent, "ONLINE BRONNEN", 11, PURPLE, true, 18);
            if (!data.chapter.isEmpty()) addText(detailContent, data.chapter, 14, WHITE, true, 7);
            for (GeminiClient.Source source : data.sources) {
                TextView link = text("↗  " + source.title, 12, PURPLE, false);
                link.setPaintFlags(link.getPaintFlags() |
                        android.graphics.Paint.UNDERLINE_TEXT_FLAG);
                LinearLayout.LayoutParams linkp = lp(-1, -2);
                linkp.topMargin = dp(9);
                detailContent.addView(link, linkp);
                link.setOnClickListener(v -> {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(source.url)));
                    } catch (Exception ignored) {
                        Toast.makeText(this, "Bron kan niet geopend worden.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        showingResults = true;
        updateUiStage();
        // The result title stays below the system status bar, even with scroll insets.
        scroll.post(() -> scroll.smoothScrollTo(0, 0));
    }
    private String translateConfidence(String s) {
        if ("high".equalsIgnoreCase(s)) return "hoog (AI-inschatting)";
        if ("medium".equalsIgnoreCase(s)) return "gemiddeld (AI-inschatting)";
        return "laag / niet zeker";
    }
    @Override protected void onDestroy() {
        super.onDestroy();
        tasks.shutdown();
    }
}
