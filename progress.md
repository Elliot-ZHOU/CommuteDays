# CommuteDays — 项目进度

> 本文件供 Agent 快速了解项目。仅在 Hui 明确要求写入时更新；平时只读。

**最后更新：** 2026-10-17

---

## 一句话

简易 Android App「跨境通勤」：手动记录跨境通勤总天数与刷关天数，含口岸选择、历史编辑、本地持久化。

---

## 基本信息

| 项 | 内容 |
|----|------|
| 本地路径 | `/Users/zhouhui/Downloads/MiniProgram` |
| App 名 | 跨境通勤（工程/仓库名 CommuteDays） |
| 包名 | `com.hui.commute` |
| 技术栈 | Kotlin + Jetpack Compose，minSdk 26，compileSdk 35 |
| 持久化 | SharedPreferences（历史为单一数据源，计数由历史推导） |
| GitHub | https://github.com/Elliot-ZHOU/CommuteDays （公开，`main`） |
| 账号 | Elliot-ZHOU |

---

## 已实现功能

1. **首页计数**：总天数、刷关天数，各自 + / −；左上角 + 补录历史。
2. **联动规则**：刷关 +1 → 总天数也 +1（历史只写一条并标「（刷关）」）；总天数 +1 → 刷关不变。
3. **口岸**：加总天数 / 加刷关 / 补录历史时弹窗选择；固定六口岸：罗湖、莲塘、福田、皇岗、深圳湾、文锦渡。
4. **同日唯一**：每个日历日全 App 最多一条历史；重复 → 提示「已添加」，不改数据。
5. **历史页**：按新→旧列表，显示日期、口岸、可选「（刷关）」；可编辑日期/口岸/是否刷关、可删除；变更立即同步首页计数。
6. **计数公式**：总天数 = 历史行数；刷关天数 = `is刷关` 为 true 的行数。
7. **− 快捷**：刷关 − 删最近一条刷关记录；总天数 − 只删最近一条非刷关记录。
8. **底部导航**：计数 ↔ 查看记录。
9. **本地存储**：杀进程后数据仍在。

---

## 关键代码位置

- `app/.../MainActivity.kt` — 导航与对话框接线
- `app/.../data/HistoryRepository.kt` — 持久化、同日校验、增删改
- `app/.../data/HistoryRecord.kt` / `Ports.kt` — 模型与口岸列表
- `app/.../ui/CommuteViewModel.kt` — UI 状态与计数
- `app/.../ui/CounterScreen.kt` / `HistoryScreen.kt` / `Dialogs.kt` — 界面

---

## 流程与协作约定

- 功能开发可走 **feature-pipeline**：Prompt Writer → Programmer → Auditor（见 `.cursor/skills/feature-pipeline` 与个人 Agent Store 对应 skill）。
- **`progress.md`**：新 Agent 开场先读；**仅当 Hui 明确说写入/更新 progress 时才改本文件**。

---

## 当前进度状态

- [x] 需求经 Prompt Writer 多轮定稿
- [x] Programmer 实现；Auditor **PASS**
- [x] 真机安装运行成功
- [x] 已能打 debug APK（`Build → Build APK(s)` → `app/build/outputs/apk/debug/`）
- [x] 代码已推送到 GitHub `Elliot-ZHOU/CommuteDays`
- [ ] （可选）正式 release 签名 APK
- [ ] （可选）应用商店上架 / 图标与文案打磨

---

## 已知说明

- 代理环境曾无法本机编译；以 Android Studio Sync + 真机/模拟器运行为准。
- `.cursor/` 已在 `.gitignore`，不上传 GitHub；`progress.md` 在仓库根目录，会随代码提交（若 Hui 之后要求提交）。
