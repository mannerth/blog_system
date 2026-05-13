<script lang="ts" setup>
import Quill, { Delta } from 'quill';
import 'quill/dist/quill.snow.css';
import hljs from 'highlight.js';
import { onMounted, ref, watch } from 'vue';
import 'highlight.js/styles/vs2015.css';

const editorRef = ref<HTMLDivElement>();
let quill: Quill;
const props = defineProps({
  content: {
    type: Delta,
    default: new Delta()
  },
  read: {
    type: Boolean,
    default: false
  },
});
let emits = defineEmits(['update:content']);
const setDeltaContent = (cont: Delta)=>{
  quill.setContents(cont);
}
defineExpose({
  setDeltaContent
})

onMounted(() => {
  // 确保DOM元素已加载
  if (editorRef.value) {
    const quillOption = {
      readOnly: props.read,
      theme: 'snow',
      placeholder: '请输入内容...',
      modules: {
        toolbar: props.read? null :
        [
          ['bold', 'italic', 'underline', 'strike'], // 加粗 斜体 下划线 删除线
          ['blockquote', 'code-block', 'code'], // 引用  代码块
          [{ list: 'ordered' }, { list: 'bullet' }], // 有序、无序列表
          [{ indent: '-1' }, { indent: '+1' }], // 缩进
          [{ header: [1, 2, 3, 4, 5, 6, false] }], // 标题
          [{ color: [] }, { background: [] }], // 字体颜色、字体背景颜色
          [{ align: [] }], // 对齐方式
          ['clean'], // 清除文本格式
          ['link', 'image', 'video'] // 链接、图片、视频
        ],
        syntax: { hljs },
      }
    };
    // 初始化Quill编辑器
    quill = new Quill(editorRef.value, quillOption);

    quill.setContents(props.content);
    quill.on('text-change', (_delta, _oldContent, _source)=>{
      emits('update:content', quill.getContents());
    });
  } else {
    console.error('编辑器容器元素不存在');
  }
});

if(props.read){watch(
  () => props.content, // 监听 props.content
  (newContent) => {
    if (quill) { // 确保 Quill 实例已初始化
      quill.setContents(newContent); // 内容更新时重新设置
    }
  },
  { deep: true } // Delta 是对象，需要深度监听
);}

// ///获取Delta内容
// function getDelta():Delta{
//   return quill.getContents();
// }
// ///获取html内容
// function getHTML():string{
//   return quill.getSemanticHTML();
// }
</script>

<template>
  <div class="container">
    <div ref="editorRef" class="editor">
    </div>
  </div>
</template>

<style>
div.container {
    resize: both;
    height: 600px;
    padding: 0;
}

.editor {
    width: 100%;
    height: 100%;
}

/* Quill modern card look */
.ql-toolbar.ql-snow {
    border: 1px solid var(--color-border);
    border-bottom: 0;
    border-top-left-radius: var(--radius-md);
    border-top-right-radius: var(--radius-md);
    background: #fff;
}
.ql-container.ql-snow {
    border: 1px solid var(--color-border);
    border-bottom-left-radius: var(--radius-md);
    border-bottom-right-radius: var(--radius-md);
    background: #fff;
    box-shadow: var(--shadow-md);
}
.ql-editor {
    font-size: 15px;
    line-height: 1.8;
    color: var(--color-text);
    padding: 16px 18px;
}
.ql-editor a { color: var(--color-primary); text-decoration: underline; }
.ql-picker, .ql-stroke, .ql-fill { color: var(--color-text); stroke: var(--color-text); }
.ql-toolbar .ql-picker-label:hover, .ql-toolbar .ql-picker-item:hover, .ql-toolbar button:hover {
    background: #f5f7fa;
    border-radius: 6px;
}

/* Placeholder */
.ql-editor.ql-blank::before {
    color: var(--color-muted);
    font-style: normal;
}

/* Dark theme adaptation */
[data-theme="dark"] .ql-toolbar.ql-snow {
    background: #0b1220;
    border-color: var(--color-border);
}
[data-theme="dark"] .ql-container.ql-snow {
    background: #0f172a;
    border-color: var(--color-border);
}
[data-theme="dark"] .ql-editor { color: var(--color-text); }
[data-theme="dark"] .ql-editor a { color: #93c5fd; }
[data-theme="dark"] .ql-picker, [data-theme="dark"] .ql-stroke, [data-theme="dark"] .ql-fill {
    color: var(--color-text);
    stroke: var(--color-text);
}

/* Mobile tweaks */
@media (max-width: 768px){
  div.container{ height: 420px; }
  .ql-editor{ padding: 12px; }
}
</style>
