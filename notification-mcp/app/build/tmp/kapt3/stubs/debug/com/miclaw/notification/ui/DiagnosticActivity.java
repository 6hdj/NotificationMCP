package com.miclaw.notification.ui;

/**
 * 一键故障排查 UI 界面
 *
 * 用户可以：
 * 1. 点击「开始诊断」执行全链路排查
 * 2. 查看每个检查项的详细结果
 * 3. 点击「一键修复」自动修复所有可修复的问题
 * 4. 点击单个失败项跳转到对应的设置页面
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u001e\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00172\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u001a0\u0019H\u0002J\u0010\u0010\u001b\u001a\u00020\u00152\u0006\u0010\u001c\u001a\u00020\u001aH\u0002J\u0010\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020\tH\u0002J\b\u0010 \u001a\u00020\u001eH\u0002J\b\u0010!\u001a\u00020\u001eH\u0002J\u0012\u0010\"\u001a\u00020\u001e2\b\u0010#\u001a\u0004\u0018\u00010$H\u0014J\b\u0010%\u001a\u00020\u001eH\u0002J\b\u0010&\u001a\u00020\u001eH\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082.\u00a2\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0011X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0011X\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006\'"}, d2 = {"Lcom/miclaw/notification/ui/DiagnosticActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "btnAutoFix", "Landroid/widget/Button;", "btnDiagnose", "containerChecks", "Landroid/widget/LinearLayout;", "lastReport", "Lcom/miclaw/notification/diagnostic/DiagnosticReport;", "progressBar", "Landroid/widget/ProgressBar;", "scope", "Lkotlinx/coroutines/CoroutineScope;", "scrollResult", "Landroid/widget/ScrollView;", "tvStats", "Landroid/widget/TextView;", "tvStatus", "tvSummary", "createCategoryHeader", "Landroid/view/View;", "category", "Lcom/miclaw/notification/diagnostic/CheckCategory;", "items", "", "Lcom/miclaw/notification/diagnostic/CheckItem;", "createCheckItemView", "item", "displayReport", "", "report", "executeAutoFix", "initViews", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "setupListeners", "startDiagnosis", "app_debug"})
public final class DiagnosticActivity extends androidx.appcompat.app.AppCompatActivity {
    private android.widget.TextView tvStatus;
    private android.widget.TextView tvSummary;
    private android.widget.ProgressBar progressBar;
    private android.widget.Button btnDiagnose;
    private android.widget.Button btnAutoFix;
    private android.widget.LinearLayout containerChecks;
    private android.widget.ScrollView scrollResult;
    private android.widget.TextView tvStats;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.CoroutineScope scope = null;
    @org.jetbrains.annotations.Nullable()
    private com.miclaw.notification.diagnostic.DiagnosticReport lastReport;
    
    public DiagnosticActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void initViews() {
    }
    
    private final void setupListeners() {
    }
    
    private final void startDiagnosis() {
    }
    
    private final void displayReport(com.miclaw.notification.diagnostic.DiagnosticReport report) {
    }
    
    private final android.view.View createCategoryHeader(com.miclaw.notification.diagnostic.CheckCategory category, java.util.List<com.miclaw.notification.diagnostic.CheckItem> items) {
        return null;
    }
    
    private final android.view.View createCheckItemView(com.miclaw.notification.diagnostic.CheckItem item) {
        return null;
    }
    
    private final void executeAutoFix() {
    }
}