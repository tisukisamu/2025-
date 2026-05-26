import request from '../utils/request'
import type { Result, PageResponse, PageRequest } from '../types'
import type { Message, MessageRequest } from '../types/message'

export function getAlbumMessages(albumId: number): Promise<Result<Message[]>> {
  return request({
    url: `/messages/album/${albumId}`,
    method: 'get'
  })
}

export function getAlbumMessagesPage(albumId: number, params: PageRequest): Promise<Result<PageResponse<Message>>> {
  return request({
    url: `/messages/album/${albumId}/page`,
    method: 'get',
    params
  })
}

export function createMessage(data: MessageRequest): Promise<Result<Message>> {
  return request({
    url: '/messages',
    method: 'post',
    data
  })
}

export function deleteMessage(id: number): Promise<Result<void>> {
  return request({
    url: `/messages/${id}`,
    method: 'delete'
  })
}
