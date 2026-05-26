import { computed, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import { useUserStore } from '../../stores/user'
import { createCommunityPost, createPostComment, deleteCommunityPost, deletePostComment, getCommunityPosts } from '../../api/community'
import { deleteFile, uploadImage } from '../../api/upload'
import type { CommunityComment, CommunityPost } from '../../types'

const userStore = useUserStore()
const loading = ref(false)
const posting = ref(false)
const uploading = ref(false)
const rows = ref<CommunityPost[]>([])
const commentDrafts = reactive<Record<number, string>>({})
const postForm = reactive({
  content: '',
  imagePath: ''
})

const myPosts = computed(() => {
  const currentId = Number(userStore.userInfo?.id)
  return rows.value.filter((post) => Number(post.userId) === currentId)
})

const formatDate = (v: string) => {
  if (!v) return ''
  return String(v).replace('T', ' ').slice(0, 16)
}

const fetchPosts = async () => {
  loading.value = true
  try {
    const res = await getCommunityPosts()
    rows.value = res.data || []
  } finally {
    loading.value = false
  }
}

const handleUpload = async (option: any) => {
  uploading.value = true
  try {
    const file = option?.file as File
    const res = await uploadImage(file, 'community')
    postForm.imagePath = res.data?.url || ''
    option?.onSuccess?.(res)
    message.success('图片上传成功')
  } catch (error) {
    option?.onError?.(error)
  } finally {
    uploading.value = false
  }
}

const removeImage = async () => {
  if (!postForm.imagePath) return
  const path = postForm.imagePath
  postForm.imagePath = ''
  try {
    await deleteFile(path)
  } catch (_e) {
  }
}

const submitPost = async () => {
  if (!postForm.content.trim()) {
    message.warning('请输入分享内容')
    return false
  }
  posting.value = true
  try {
    await createCommunityPost({
      content: postForm.content.trim(),
      imagePath: postForm.imagePath || null
    })
    postForm.content = ''
    postForm.imagePath = ''
    message.success('发布成功')
    await fetchPosts()
    return true
  } finally {
    posting.value = false
  }
}

const submitComment = async (postId: number) => {
  const content = String(commentDrafts[postId] || '').trim()
  if (!content) {
    message.warning('请输入评论内容')
    return
  }
  await createPostComment(postId, { content })
  commentDrafts[postId] = ''
  await fetchPosts()
}

const canDeletePost = (post: CommunityPost) => {
  return userStore.isAdmin || Number(userStore.userInfo?.id) === Number(post.userId)
}

const canDeleteComment = (comment: CommunityComment) => {
  return userStore.isAdmin || Number(userStore.userInfo?.id) === Number(comment.userId)
}

const removePost = async (postId: number) => {
  await deleteCommunityPost(postId)
  message.success('已删除动态')
  await fetchPosts()
}

const removeComment = async (commentId: number) => {
  await deletePostComment(commentId)
  message.success('已删除评论')
  await fetchPosts()
}

export function useCommunity() {
  return {
    loading,
    posting,
    uploading,
    rows,
    myPosts,
    commentDrafts,
    postForm,
    formatDate,
    fetchPosts,
    handleUpload,
    removeImage,
    submitPost,
    submitComment,
    canDeletePost,
    canDeleteComment,
    removePost,
    removeComment
  }
}
