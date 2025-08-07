import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { calculateFileHash, formatFileSize, checkFileType, checkFileSize } from '@/utils/fileHash'
import { checkFile, uploadFileEnhanced, updateFileReference } from '@/api/uploadEnhanced'

/**
 * 增强的文件上传组合式函数
 * 支持文件去重、秒传、进度显示等功能
 */
export function useFileUploadEnhanced(options = {}) {
  // 配置选项
  const {
    maxSize = 10 * 1024 * 1024, // 默认10MB
    allowedTypes = ['image/jpeg', 'image/png', 'image/gif'],
    category = 'default',
    entityType = null,
    entityId = null,
    fieldName = null,
    onSuccess = null,
    onError = null
  } = options

  // 状态
  const uploading = ref(false)
  const uploadProgress = ref(0)
  const calculating = ref(false)
  const fileInfo = ref(null)

  /**
   * 处理文件上传
   * @param {File} file - 要上传的文件
   * @returns {Promise}
   */
  const handleUpload = async (file) => {
    try {
      // 验证文件
      if (!checkFileType(file, allowedTypes)) {
        throw new Error(`只允许上传 ${allowedTypes.join(', ')} 类型的文件`)
      }

      if (!checkFileSize(file, maxSize)) {
        throw new Error(`文件大小不能超过 ${formatFileSize(maxSize)}`)
      }

      // 计算文件哈希
      calculating.value = true
      ElMessage.info('正在计算文件特征...')
      
      const fileHash = await calculateFileHash(file)
      calculating.value = false

      // 检查文件是否已存在
      const checkResult = await checkFile({
        fileHash,
        fileSize: file.size,
        fileName: file.name,
        mimeType: file.type,
        fileCategory: category
      })

      if (checkResult.data.exists) {
        // 文件已存在，执行秒传
        ElMessage.success('文件已存在，秒传成功！')
        
        const result = {
          fileId: checkResult.data.fileId,
          url: checkResult.data.url,
          quickUpload: true,
          fileInfo: checkResult.data.fileInfo
        }

        // 创建文件引用
        if (entityType && entityId && fieldName) {
          await updateFileReference({
            fileId: result.fileId,
            entityType,
            entityId,
            fieldName
          })
        }

        fileInfo.value = result
        if (onSuccess) {
          onSuccess(result)
        }
        
        return result
      }

      // 文件不存在，执行实际上传
      uploading.value = true
      uploadProgress.value = 0

      const formData = new FormData()
      formData.append('file', file)
      formData.append('hash', fileHash)
      formData.append('category', category)

      // 创建带进度的上传请求
      const response = await uploadFileEnhanced(formData)

      const result = {
        fileId: response.data.fileId,
        url: response.data.url,
        quickUpload: false,
        fileInfo: response.data
      }

      // 创建文件引用
      if (entityType && entityId && fieldName) {
        await updateFileReference({
          fileId: result.fileId,
          entityType,
          entityId,
          fieldName
        })
      }

      fileInfo.value = result
      ElMessage.success('文件上传成功！')
      
      if (onSuccess) {
        onSuccess(result)
      }
      
      return result

    } catch (error) {
      console.error('文件上传失败:', error)
      ElMessage.error(error.message || '文件上传失败')
      
      if (onError) {
        onError(error)
      }
      
      throw error
    } finally {
      uploading.value = false
      uploadProgress.value = 0
      calculating.value = false
    }
  }

  /**
   * 处理Element Plus的上传组件
   * @param {Object} uploadFile - Element Plus上传组件的文件对象
   */
  const handleElUpload = async (uploadFile) => {
    if (!uploadFile.raw) {
      throw new Error('无效的文件对象')
    }
    return handleUpload(uploadFile.raw)
  }

  /**
   * 清空文件信息
   */
  const clearFile = () => {
    fileInfo.value = null
    uploadProgress.value = 0
  }

  return {
    // 状态
    uploading,
    uploadProgress,
    calculating,
    fileInfo,
    
    // 方法
    handleUpload,
    handleElUpload,
    clearFile,
    
    // 工具函数
    formatFileSize
  }
}