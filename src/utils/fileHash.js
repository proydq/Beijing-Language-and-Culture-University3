/**
 * 文件哈希计算工具
 * 在浏览器端计算文件的SHA-256哈希值
 */

/**
 * 计算文件的SHA-256哈希值
 * @param {File} file - 要计算哈希的文件
 * @returns {Promise<string>} 文件的哈希值（十六进制字符串）
 */
export async function calculateFileHash(file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    
    reader.onload = async (e) => {
      try {
        const arrayBuffer = e.target.result
        const hashBuffer = await crypto.subtle.digest('SHA-256', arrayBuffer)
        const hashArray = Array.from(new Uint8Array(hashBuffer))
        const hashHex = hashArray.map(b => b.toString(16).padStart(2, '0')).join('')
        resolve(hashHex)
      } catch (error) {
        reject(error)
      }
    }
    
    reader.onerror = () => {
      reject(new Error('读取文件失败'))
    }
    
    reader.readAsArrayBuffer(file)
  })
}

/**
 * 计算大文件的哈希值（分片计算）
 * @param {File} file - 要计算哈希的文件
 * @param {Function} onProgress - 进度回调函数
 * @returns {Promise<string>} 文件的哈希值
 */
export async function calculateLargeFileHash(file, onProgress) {
  const chunkSize = 2 * 1024 * 1024 // 2MB chunks
  const chunks = Math.ceil(file.size / chunkSize)
  let currentChunk = 0
  
  const crypto = window.crypto || window.msCrypto
  const hashAlgorithm = 'SHA-256'
  
  // 不支持增量哈希，需要读取整个文件
  const arrayBuffers = []
  
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    
    const readNextChunk = () => {
      const start = currentChunk * chunkSize
      const end = Math.min(start + chunkSize, file.size)
      const chunk = file.slice(start, end)
      reader.readAsArrayBuffer(chunk)
    }
    
    reader.onload = async (e) => {
      arrayBuffers.push(e.target.result)
      currentChunk++
      
      if (onProgress) {
        onProgress(Math.min(currentChunk / chunks * 100, 100))
      }
      
      if (currentChunk < chunks) {
        readNextChunk()
      } else {
        // 合并所有chunks
        const totalLength = arrayBuffers.reduce((acc, buf) => acc + buf.byteLength, 0)
        const combined = new Uint8Array(totalLength)
        let offset = 0
        
        for (const buffer of arrayBuffers) {
          combined.set(new Uint8Array(buffer), offset)
          offset += buffer.byteLength
        }
        
        try {
          const hashBuffer = await crypto.subtle.digest(hashAlgorithm, combined)
          const hashArray = Array.from(new Uint8Array(hashBuffer))
          const hashHex = hashArray.map(b => b.toString(16).padStart(2, '0')).join('')
          resolve(hashHex)
        } catch (error) {
          reject(error)
        }
      }
    }
    
    reader.onerror = () => {
      reject(new Error('读取文件失败'))
    }
    
    readNextChunk()
  })
}

/**
 * 格式化文件大小
 * @param {number} bytes - 字节数
 * @returns {string} 格式化后的文件大小
 */
export function formatFileSize(bytes) {
  if (bytes === 0) return '0 B'
  
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

/**
 * 检查文件类型
 * @param {File} file - 文件
 * @param {string[]} allowedTypes - 允许的MIME类型
 * @returns {boolean} 是否允许
 */
export function checkFileType(file, allowedTypes) {
  return allowedTypes.includes(file.type)
}

/**
 * 检查文件大小
 * @param {File} file - 文件
 * @param {number} maxSize - 最大大小（字节）
 * @returns {boolean} 是否在限制内
 */
export function checkFileSize(file, maxSize) {
  return file.size <= maxSize
}