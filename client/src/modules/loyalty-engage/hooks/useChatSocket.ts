'use client';

import { useEffect } from 'react';
import { useSocket } from '@/components/providers/socket-provider';
import type { ChatMessage } from '@/types/loyalty-engage';

export function useChatSocket(
  chatId: number | null,
  onNewMessage: (message: ChatMessage) => void
) {
  const { socket, isConnected } = useSocket();

  useEffect(() => {
    if (!socket || chatId === null) return;

    const handleNewMessage = (message: ChatMessage) => {
      if (message.chatId === chatId) onNewMessage(message);
    };

    socket.on('new_message', handleNewMessage);
    return () => {
      socket.off('new_message', handleNewMessage);
    };
  }, [socket, chatId, onNewMessage]);

  return { isConnected };
}
