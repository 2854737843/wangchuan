# 毕业答辩PPT（Markdown版，可一键转换）

本目录提供一份适合本科毕业答辩的 PPT 内容（以 Markdown 编写），并给出两种常见的“Markdown → PPT”转换方法。

## 目录

- `defense.md`：答辩PPT内容（可直接编辑）
- `assets/`：放截图与图片（登录页、任务页、周报页、启动截图等）

## 你需要先补的图片（建议）

把你的截图放到 `assets/`，然后在 `defense.md` 里按相对路径引用，例如：

```markdown
![](assets/login.png)
```

建议准备：
- 登录页截图
- 导师端：周报审核页截图
- 学生端：周报提交页截图
- 任务列表/任务指派截图
- 前端启动成功控制台截图
- 后端启动成功控制台截图

---

## 方法A：用 Marp 把 Markdown 转成 PPTX（最简单）

### 1. 安装（Windows）

先安装 Node.js（建议 18+）。然后在命令行执行：

```bash
npm i -g @marp-team/marp-cli
```

### 2. 生成 PPTX

在仓库根目录执行：

```bash
marp docs/defense-ppt/defense.md --pptx --allow-local-files -o docs/defense-ppt/答辩PPT.pptx
```

生成后你会得到：`docs/defense-ppt/答辩PPT.pptx`

### 3. 常见问题

- 如果图片不显示：确认图片在 `assets/` 且命令带 `--allow-local-files`
- 字体不满意：可以在 `defense.md` 里加简单主题或 CSS（后续再调也行）

---

## 方法B：用 Pandoc 转 PPTX（也常用）

> Pandoc 需要安装：Pandoc +（Windows 下建议）PowerPoint 支持组件。

基本命令示例：

```bash
pandoc docs/defense-ppt/defense.md -o docs/defense-ppt/答辩PPT.pptx
```

如果不顺利，优先用方法A（Marp）。
