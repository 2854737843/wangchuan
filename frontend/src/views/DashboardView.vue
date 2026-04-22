<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import client from '../api/client'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const auth = useAuthStore()
const orgs = ref([])
const members = ref([])
const projects = ref([])
const topics = ref([])
const tasks = ref([])
const selectedProject = ref('')
const selectedTopic = ref('')
const projectForm = reactive({ name: '', description: '' })
const topicForm = reactive({ title: '', description: '' })
const taskForm = reactive({ title: '', description: '', status: 'TODO' })
const memberForm = reactive({ username: '', role: 'STUDENT' })

const loadOrgs = async () => {
  const { data } = await client.get('/api/orgs/mine')
  orgs.value = data.data.map((org) => ({ ...org, id: String(org.id) }))
  if (!auth.orgId && orgs.value.length) auth.setOrgId(orgs.value[0].id)
}

const loadMembers = async () => {
  if (!auth.orgId) return
  const { data } = await client.get(`/api/orgs/${auth.orgId}/members`)
  members.value = data.data
}

const loadProjects = async () => {
  if (!auth.orgId) return
  const { data } = await client.get('/api/projects')
  projects.value = data.data
}

const loadTopics = async () => {
  if (!selectedProject.value) return (topics.value = [])
  const { data } = await client.get(`/api/projects/${selectedProject.value}/topics`)
  topics.value = data.data
}

const loadTasks = async () => {
  if (!selectedTopic.value) return (tasks.value = [])
  const { data } = await client.get(`/api/topics/${selectedTopic.value}/tasks`)
  tasks.value = data.data
}

const createProject = async () => {
  await client.post('/api/projects', projectForm)
  projectForm.name = ''
  projectForm.description = ''
  await loadProjects()
}

const createTopic = async () => {
  if (!selectedProject.value) return ElMessage.warning('请先选择项目')
  await client.post(`/api/projects/${selectedProject.value}/topics`, topicForm)
  topicForm.title = ''
  topicForm.description = ''
  await loadTopics()
}

const createTask = async () => {
  if (!selectedTopic.value) return ElMessage.warning('请先选择课题')
  await client.post(`/api/topics/${selectedTopic.value}/tasks`, taskForm)
  taskForm.title = ''
  taskForm.description = ''
  taskForm.status = 'TODO'
  await loadTasks()
}

const addMember = async () => {
  await client.post(`/api/orgs/${auth.orgId}/members`, memberForm)
  memberForm.username = ''
  memberForm.role = 'STUDENT'
  await loadMembers()
}

const refreshAll = async () => {
  await loadMembers()
  await loadProjects()
  await loadTopics()
  await loadTasks()
}

const onOrgChanged = async (value) => {
  auth.setOrgId(value)
  selectedProject.value = ''
  selectedTopic.value = ''
  await refreshAll()
}

const logout = () => {
  auth.logout()
  router.push('/login')
}

onMounted(async () => {
  try {
    await loadOrgs()
    await refreshAll()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '加载失败')
  }
})
</script>

<template>
  <div class="dashboard">
    <el-space wrap>
      <el-select :model-value="auth.orgId" placeholder="选择课题组" @change="onOrgChanged">
        <el-option v-for="org in orgs" :key="org.id" :label="`${org.name} (${org.role})`" :value="org.id" />
      </el-select>
      <el-button @click="refreshAll">刷新</el-button>
      <el-button type="danger" plain @click="logout">退出</el-button>
    </el-space>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="6">
        <el-card>
          <template #header>成员管理</template>
          <el-form>
            <el-input v-model="memberForm.username" placeholder="用户名" style="margin-bottom: 8px" />
            <el-select v-model="memberForm.role" style="width: 100%; margin-bottom: 8px">
              <el-option label="OWNER" value="OWNER" />
              <el-option label="TEACHER" value="TEACHER" />
              <el-option label="STUDENT" value="STUDENT" />
            </el-select>
            <el-button type="primary" @click="addMember">添加成员</el-button>
          </el-form>
          <el-table :data="members" size="small" style="margin-top: 12px">
            <el-table-column prop="username" label="用户名" />
            <el-table-column prop="role" label="角色" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <template #header>项目</template>
          <el-input v-model="projectForm.name" placeholder="项目名" style="margin-bottom: 8px" />
          <el-input v-model="projectForm.description" placeholder="描述" style="margin-bottom: 8px" />
          <el-button type="primary" @click="createProject">新建项目</el-button>
          <el-select v-model="selectedProject" placeholder="选择项目" style="width: 100%; margin-top: 12px" @change="loadTopics">
            <el-option v-for="item in projects" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <template #header>课题</template>
          <el-input v-model="topicForm.title" placeholder="课题标题" style="margin-bottom: 8px" />
          <el-input v-model="topicForm.description" placeholder="描述" style="margin-bottom: 8px" />
          <el-button type="primary" @click="createTopic">新建课题</el-button>
          <el-select v-model="selectedTopic" placeholder="选择课题" style="width: 100%; margin-top: 12px" @change="loadTasks">
            <el-option v-for="item in topics" :key="item.id" :label="item.title" :value="item.id" />
          </el-select>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <template #header>任务</template>
          <el-input v-model="taskForm.title" placeholder="任务标题" style="margin-bottom: 8px" />
          <el-input v-model="taskForm.description" placeholder="描述" style="margin-bottom: 8px" />
          <el-select v-model="taskForm.status" style="width: 100%; margin-bottom: 8px">
            <el-option label="TODO" value="TODO" />
            <el-option label="DOING" value="DOING" />
            <el-option label="DONE" value="DONE" />
          </el-select>
          <el-button type="primary" @click="createTask">新建任务</el-button>
          <el-table :data="tasks" size="small" style="margin-top: 12px">
            <el-table-column prop="title" label="标题" />
            <el-table-column prop="status" label="状态" width="80" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.dashboard { padding: 16px; background: #f5f7fa; min-height: 100vh; }
</style>
