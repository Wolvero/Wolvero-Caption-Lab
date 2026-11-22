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
    private TextView videoName, sizeLabel, progressLabel, keyState, tavilyState;
    private ProgressBar progressBar;
    private Button analyzeButton;
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

    private void buildScreen() {
        scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(BG);
        scroll.setClipToPadding(false);
        scroll.setOnApplyWindowInsetsListener((view, insets) -> {
            view.setPadding(0, insets.getSystemWindowInsetTop(), 0, insets.getSystemWindowInsetBottom());
            return insets;
        });
        content = vertical();
        content.setPadding(dp(20), dp(26), dp(20), dp(36));
        scroll.addView(content, new ScrollView.LayoutParams(-1, -2));
        setContentView(scroll);

        LinearLayout banner = horizontal();
        TextView mark = text("W/", 25, PURPLE, true);
        banner.addView(mark, lp(46, -2));
        LinearLayout brand = vertical();
        brand.addView(text("WOLVERO", 17, WHITE, true), lp(-1, -2));
        TextView sub = text("C A P T I O N   L A B", 9, MUTED, true);
        LinearLayout.LayoutParams subp = lp(-1, -2);
        subp.topMargin = dp(3);
        brand.addView(sub, subp);
        banner.addView(brand, new LinearLayout.LayoutParams(0, -2, 1f));
        TextView version = text("BETA 01", 10, GOOD, true);
        version.setBackground(shape(INNER, 8, STROKE));
        pad(version, 10, 8);
        banner.addView(version, lp(-2, -2));
        content.addView(banner, lp(-1, -2));

        addText(content, "From gameplay to\ngreat captions.", 29, WHITE, true, 35);
        addText(content, "Upload een clip. De app bekijkt het spel, luistert naar het geluid en schrijft vijf Engelstalige TikTok-captions.", 14, MUTED, false, 13);
        addSpacer(content, 25);

        LinearLayout uploadCard = card();
        content.addView(uploadCard, lp(-1, -2));
        addText(uploadCard, "01   JOUW VIDEO", 12, PURPLE, true, 0);
        addText(uploadCard, "Welk moment gaan we captionen?", 16, WHITE, true, 12);

        previewBox = vertical();
        previewBox.setGravity(Gravity.CENTER);
        previewBox.setBackground(shape(INNER, 14, STROKE));
        LinearLayout.LayoutParams pp = lp(-1, -2);
        pp.topMargin = dp(16);
        uploadCard.addView(previewBox, pp);
        pad(previewBox, 13, 15);
        previewImage = new ImageView(this);
        previewImage.setAdjustViewBounds(true);
        previewImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        previewImage.setVisibility(View.GONE);
        previewBox.addView(previewImage, lp(-1, 170));
        videoName = text("Geen video geselecteerd", 14, WHITE, true);
        videoName.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams nameLp = lp(-1, -2);
        nameLp.topMargin = dp(10);
        previewBox.addView(videoName, nameLp);
        sizeLabel = text("MP4 of andere ondersteunde video · max. 80 MB", 12, MUTED, false);
        sizeLabel.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams sizeLp = lp(-1, -2);
        sizeLp.topMargin = dp(7);
        previewBox.addView(sizeLabel, sizeLp);
        addSpacer(uploadCard, 14);
        Button choose = action("＋  Kies video uit galerij", false, v -> pickVideo());
        uploadCard.addView(choose, lp(-1, 50));

        addSpacer(content, 14);
        LinearLayout settings = card();
        content.addView(settings, lp(-1, -2));
        addText(settings, "02   GRATIS AI-VERBINDING", 12, PURPLE, true, 0);
        addText(settings, "Jouw eigen Gemini API-sleutel", 16, WHITE, true, 11);
        keyState = text("Nog niet ingesteld", 12, MUTED, false);
        LinearLayout.LayoutParams kp = lp(-1, -2);
        kp.topMargin = dp(8);
        settings.addView(keyState, kp);
        addSpacer(settings, 13);
        settings.addView(action("🔑  API-sleutel instellen", false, v -> showKeyDialog()), lp(-1, 48));
        addText(settings, "Gebruik een gratis project zonder billing. De sleutel wordt versleuteld op je telefoon bewaard.", 11, MUTED, false, 12);
        addSpacer(settings, 20);
        addText(settings, "GRATIS ONLINE ZOEKFUNCTIE", 12, PURPLE, true, 0);
        addText(settings, "Tavily · 1.000 zoekcredits per maand", 14, WHITE, true, 9);
        tavilyState = text("Optioneel, voor blijvende verificatie van games en hoofdstukken", 12, MUTED, false);
        LinearLayout.LayoutParams tavp = lp(-1, -2);
        tavp.topMargin = dp(7);
        settings.addView(tavilyState, tavp);
        addSpacer(settings, 13);
        settings.addView(action("🌐  Gratis zoek-sleutel instellen", false, v -> showTavilyDialog()), lp(-1, 48));
        addText(settings, "Gratis Tavily Researcher-account, zonder kredietkaart of betaalplan. Eén zoekopdracht per video.", 11, MUTED, false, 12);
        addSpacer(content, 14);

        analyzeButton = action("ANALYSEER VIDEO   →", true, v -> beginAnalysis());
        content.addView(analyzeButton, lp(-1, 58));
        setButtonReady();
        addText(content, "Geen abonnement. De gratis API heeft wel gebruikslimieten. Je video wordt voor analyse naar Google verstuurd.", 11, MUTED, false, 12);

        progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.GONE);
        LinearLayout.LayoutParams barp = lp(40, 40);
        barp.gravity = Gravity.CENTER_HORIZONTAL;
        barp.topMargin = dp(23);
        content.addView(progressBar, barp);
        progressLabel = text("", 13, PURPLE, true);
        progressLabel.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams statp = lp(-1, -2);
        statp.topMargin = dp(10);
        content.addView(progressLabel, statp);

        results = vertical();
        LinearLayout.LayoutParams rp = lp(-1, -2);
        rp.topMargin = dp(8);
        content.addView(results, rp);
        refreshKeyState();
        refreshTavilyState();
    }

    private void setButtonReady() {
        if (analyzeButton == null) return;
        boolean ready = !working && selectedVideo != null && selectedVideo.exists();
        analyzeButton.setEnabled(ready);
        analyzeButton.setAlpha(ready ? 1f : 0.48f);
    }
    private void refreshKeyState() {
        try {
            String key = SecureKeys.load(this);
            keyState.setText(key.isEmpty() ? "Nog niet ingesteld · gratis key nodig" : "✓  Sleutel veilig opgeslagen op dit toestel");
            keyState.setTextColor(key.isEmpty() ? MUTED : GOOD);
        } catch (Exception e) {
            keyState.setText("Sleutel niet toegankelijk · stel opnieuw in");
            keyState.setTextColor(MUTED);
        }
    }
    private void showKeyDialog() {
        EditText input = new EditText(this);
        input.setSingleLine(true);
        input.setTextColor(Color.BLACK);
        input.setHint("AIza...");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        int pad = dp(20);
        LinearLayout holder = vertical();
        holder.setPadding(pad, 0, pad, 0);
        holder.addView(input, lp(-1, -2));
        new AlertDialog.Builder(this)
                .setTitle("Gratis Gemini API-sleutel")
                .setMessage("Maak een gratis API-sleutel op aistudio.google.com/api-keys. Kies geen betaald project. Plak je sleutel hieronder. Deel hem niet via TikTok of GitHub.")
                .setView(holder)
                .setNegativeButton("Annuleren", null)
                .setNeutralButton("Open AI Studio", (d,w) -> {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://aistudio.google.com/api-keys")));
                })
                .setPositiveButton("Bewaar lokaal", (d,w) -> {
                    String key = input.getText().toString().trim();
                    if (key.length() < 12 || key.contains(" ")) {
                        Toast.makeText(this, "Controleer de API-sleutel.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    try {
                        SecureKeys.save(this, key);
                        refreshKeyState();
                        Toast.makeText(this, "API-sleutel versleuteld opgeslagen.", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(this, "Kon sleutel niet bewaren: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .show();
    }
    private void refreshTavilyState() {
        try {
            boolean configured = !SecureKeys.loadTavily(this).isEmpty();
            tavilyState.setText(configured ? "✓  Zoeken in spelwiki's staat aan" :
                    "Niet ingesteld · optioneel voor automatische webcontrole");
            tavilyState.setTextColor(configured ? GOOD : MUTED);
        } catch (Exception ignored) {
            tavilyState.setText("Sleutel niet toegankelijk · stel opnieuw in");
            tavilyState.setTextColor(MUTED);
        }
    }
    private void showTavilyDialog() {
        EditText input = new EditText(this);
        input.setSingleLine(true);
        input.setTextColor(Color.BLACK);
        input.setHint("tvly-...");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        LinearLayout holder = vertical();
        holder.setPadding(dp(20), 0, dp(20), 0);
        holder.addView(input, lp(-1, -2));
        new AlertDialog.Builder(this)
                .setTitle("Gratis webverificatie")
                .setMessage("Vraag een gratis Tavily API-sleutel aan. Het Researcher-plan geeft maandelijks 1.000 zoekcredits zonder kredietkaart. We gebruiken één basiszoekopdracht per video. Geen betaalplan inschakelen.")
                .setView(holder)
                .setNegativeButton("Annuleren", null)
                .setNeutralButton("Open Tavily", (d,w) -> startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://app.tavily.com"))))
                .setPositiveButton("Bewaar lokaal", (d,w) -> {
                    String key = input.getText().toString().trim();
                    if (!key.startsWith("tvly-") || key.length() < 15) {
                        Toast.makeText(this, "Controleer de Tavily API-sleutel.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    try {
                        SecureKeys.saveTavily(this, key);
                        refreshTavilyState();
                        Toast.makeText(this, "Gratis zoekverbinding ingesteld.", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(this, "Kon sleutel niet bewaren.", Toast.LENGTH_LONG).show();
                    }
                }).show();
    }

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
                    videoName.setText(name);
                    sizeLabel.setText(String.format(Locale.getDefault(), "%.1f MB · klaar om te analyseren", fileSize / 1024.0 / 1024.0));
                    showThumbnail(ready);
                    working = false;
                    setButtonReady();
                    setProgress("", false);
                    results.removeAllViews();
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
        working = true;
        setButtonReady();
        results.removeAllViews();
        setProgress("Upload starten …", true);
        tasks.execute(() -> {
            try {
                GeminiClient.Result result = GeminiClient.analyze(selectedVideo, selectedMime, apiKey, tavilyKey,
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
        addText(results, "JOUW RESULTATEN", 12, PURPLE, true, 22);
        addSpacer(results, 13);
        LinearLayout context = card();
        results.addView(context, lp(-1, -2));
        addText(context, "GEDETECTEERDE GAME", 11, MUTED, true, 0);
        addText(context, data.game, 23, WHITE, true, 7);
        addText(context, "Herkenningszekerheid: " + translateConfidence(data.confidence), 12, MUTED, false, 6);
        if (!data.scene.isEmpty()) {
            addText(context, "WAT ER GEBEURT", 11, PURPLE, true, 20);
            addText(context, data.scene, 14, WHITE, false, 7);
        }
        addText(context, "ONLINE CONTROLE", 11, PURPLE, true, 20);
        String chapter = data.chapter.isEmpty() ? "Hoofdstuk onbekend" : data.chapter;
        addText(context, chapter, 15, WHITE, true, 7);
        addText(context, data.chapterStatus, 12, data.sources.isEmpty() ? MUTED : GOOD, false, 7);
        if (!data.verificationNote.isEmpty()) addText(context, data.verificationNote, 12, MUTED, false, 7);
        for (GeminiClient.Source source : data.sources) {
            TextView link = text("↗  " + source.title, 12, PURPLE, false);
            link.setPaintFlags(link.getPaintFlags() | android.graphics.Paint.UNDERLINE_TEXT_FLAG);
            LinearLayout.LayoutParams linkp = lp(-1, -2);
            linkp.topMargin = dp(10);
            context.addView(link, linkp);
            link.setOnClickListener(v -> {
                try { startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(source.url))); }
                catch (Exception ignored) { Toast.makeText(this, "Bron kan niet geopend worden.", Toast.LENGTH_SHORT).show(); }
            });
        }
        addText(results, "05   CAPTIONS OM UIT TE KIEZEN", 12, PURPLE, true, 25);
        addSpacer(results, 14);
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
            top.addView(text(String.format(Locale.getDefault(), "%02d  %s", index, caption.style.toUpperCase(Locale.ROOT)),
                    11, PURPLE, true), new LinearLayout.LayoutParams(0, -2, 1f));
            item.addView(top, lp(-1, -2));
            addText(item, caption.text, 17, WHITE, true, 13);
            addSpacer(item, 13);
            Button copy = action("⧉  Copy caption", false, v -> {
                ClipboardManager clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                clip.setPrimaryClip(ClipData.newPlainText("Wolvero caption", caption.text));
                Toast.makeText(this, "Caption gekopieerd!", Toast.LENGTH_SHORT).show();
            });
            item.addView(copy, lp(-1, 44));
        }
        addText(results, "Tip: kies zelf de beste hook. De app kan kansen op bereik niet garanderen.", 11, MUTED, false, 5);
        scroll.post(() -> scroll.smoothScrollTo(0, results.getTop()));
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
