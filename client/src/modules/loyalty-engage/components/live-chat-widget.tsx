'use client';

import { FormEvent, useCallback, useEffect, useRef, useState } from 'react';
import { MessageCircle, Send, X } from 'lucide-react';
import { useAuth } from '@/components/providers/auth-provider';
import { chatApi } from '@/modules/loyalty-engage/api';
import { useChatSocket } from '@/modules/loyalty-engage/hooks/useChatSocket';
import { useSocket } from '@/components/providers/socket-provider';
import type { ChatMessage } from '@/types/loyalty-engage';

function appendMessage(messages: ChatMessage[], message: ChatMessage) {
  if (messages.some((existing) => existing.id === message.id)) return messages;
  return [...messages, message].sort(
    (left, right) => new Date(left.sendDate).getTime() - new Date(right.sendDate).getTime()
  );
}

export function LiveChatWidget() {
  const { user, isAuthenticated } = useAuth();
  const { isConnected } = useSocket();
  const [isOpen, setIsOpen] = useState(false);
  const [chatId, setChatId] = useState<number | null>(null);
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);
  const [sending, setSending] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const loadPromiseRef = useRef<Promise<{ chatId: number; messages: ChatMessage[] }> | null>(null);

  useEffect(() => {
    if (!isOpen || !isAuthenticated || chatId !== null) return;

    let cancelled = false;
    if (!loadPromiseRef.current) {
      setLoading(true);
      setError(null);
      loadPromiseRef.current = (async () => {
        const activeResponse = await chatApi.getActiveChat();
        const activeChat = activeResponse.data;
        const historyResponse = await chatApi.getHistory(activeChat.id);
        return { chatId: activeChat.id, messages: historyResponse.data };
      })();
    }

    const loadPromise = loadPromiseRef.current;
    void loadPromise
      .then(({ chatId: loadedChatId, messages: loadedMessages }) => {
        if (cancelled) return;
        setChatId(loadedChatId);
        setMessages(loadedMessages);
        void chatApi.markAsRead(loadedChatId).catch((cause: unknown) => {
          console.error('Không thể đánh dấu tin nhắn đã đọc:', cause);
        });
      })
      .catch((cause: unknown) => {
        console.error('Không thể tải cuộc trò chuyện:', cause);
        if (!cancelled) setError('Không thể tải cuộc trò chuyện. Vui lòng thử lại.');
      })
      .finally(() => {
        if (loadPromiseRef.current === loadPromise) loadPromiseRef.current = null;
        if (!cancelled) setLoading(false);
      });

    return () => {
      cancelled = true;
    };
  }, [isOpen, isAuthenticated, chatId]);

  const handleNewMessage = useCallback((message: ChatMessage) => {
    setMessages((current) => appendMessage(current, message));
    if (isOpen && message.senderId !== user?.id) {
      void chatApi.markAsRead(message.chatId).catch((cause: unknown) => {
        console.error('Không thể đánh dấu tin nhắn đã đọc:', cause);
      });
    }
  }, [isOpen, user?.id]);

  useChatSocket(chatId, handleNewMessage);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages, isOpen]);

  const handleSend = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const content = input.trim();
    if (!content || chatId === null || sending) return;

    setSending(true);
    setError(null);
    try {
      const response = await chatApi.sendMessage(chatId, content);
      setMessages((current) => appendMessage(current, response.data));
      setInput('');
    } catch (cause) {
      console.error('Không thể gửi tin nhắn:', cause);
      setError('Gửi tin nhắn thất bại. Vui lòng thử lại.');
    } finally {
      setSending(false);
    }
  };

  if (!isAuthenticated || user?.role?.code === 'ADMIN' || user?.role?.code === 'STAFF') return null;

  return (
    <div className="fixed bottom-4 right-4 z-50">
      {!isOpen ? (
        <button
          type="button"
          onClick={() => setIsOpen(true)}
          aria-label="Mở chat với cửa hàng"
          className="flex size-14 items-center justify-center rounded-full bg-[#1D1F20] text-white shadow-xl transition hover:bg-neutral-700 focus:outline-none focus:ring-2 focus:ring-[#84EA53]"
        >
          <MessageCircle size={24} />
        </button>
      ) : (
        <section
          aria-label="Chat hỗ trợ khách hàng"
          className="flex h-[min(520px,calc(100dvh-2rem))] w-[min(360px,calc(100vw-2rem))] flex-col overflow-hidden rounded-2xl border border-[#EAE6DF] bg-white shadow-2xl"
        >
          <header className="flex items-center justify-between bg-[#1D1F20] px-4 py-3 text-white">
            <div>
              <h2 className="text-sm font-semibold">Trò chuyện với cửa hàng</h2>
              <p className="mt-0.5 text-[11px] text-white/70">
                {isConnected ? 'Đang kết nối' : 'Đang kết nối lại...'}
              </p>
            </div>
            <button
              type="button"
              onClick={() => setIsOpen(false)}
              aria-label="Đóng chat"
              className="rounded-lg p-1 text-white/70 transition hover:bg-white/10 hover:text-white"
            >
              <X size={20} />
            </button>
          </header>

          <div className="flex-1 space-y-3 overflow-y-auto bg-[#FBF9F4] p-4">
            {loading ? (
              <p className="pt-12 text-center text-xs text-neutral-500">Đang tải tin nhắn...</p>
            ) : messages.length === 0 ? (
              <p className="pt-12 text-center text-xs text-neutral-500">
                Xin chào {user?.fullName}. Hãy gửi câu hỏi cho shop nhé!
              </p>
            ) : (
              messages.map((message) => {
                const isMe = message.senderId === user?.id;
                return (
                  <div
                    key={message.id}
                    className={`flex ${isMe ? 'justify-end' : 'justify-start'}`}
                  >
                    <div
                      className={`max-w-[82%] rounded-2xl px-3 py-2 text-sm leading-relaxed ${
                        isMe
                          ? 'rounded-br-sm bg-[#1D1F20] text-white'
                          : 'rounded-bl-sm border border-[#EAE6DF] bg-white text-neutral-800'
                      }`}
                    >
                      <p className="whitespace-pre-wrap break-words">{message.content}</p>
                      <time className={`mt-1 block text-[10px] ${isMe ? 'text-white/60' : 'text-neutral-400'}`}>
                        {new Date(message.sendDate).toLocaleTimeString('vi-VN', {
                          hour: '2-digit',
                          minute: '2-digit',
                        })}
                      </time>
                    </div>
                  </div>
                );
              })
            )}
            <div ref={messagesEndRef} />
          </div>

          {error && <p role="alert" className="px-3 pt-2 text-xs text-red-600">{error}</p>}

          <form onSubmit={handleSend} className="flex items-center gap-2 border-t border-[#EAE6DF] p-3">
            <input
              type="text"
              name="message"
              aria-label="Nội dung tin nhắn"
              value={input}
              onChange={(event) => setInput(event.target.value)}
              placeholder="Nhập nội dung tin nhắn..."
              maxLength={5000}
              disabled={loading || sending || chatId === null}
              className="h-10 min-w-0 flex-1 rounded-xl border border-[#EAE6DF] bg-[#FBF9F4] px-3 text-sm outline-none transition focus:border-neutral-900 disabled:opacity-60"
            />
            <button
              type="submit"
              aria-label="Gửi tin nhắn"
              disabled={!input.trim() || loading || sending || chatId === null}
              className="flex size-10 shrink-0 items-center justify-center rounded-xl bg-[#1D1F20] text-white transition hover:bg-neutral-700 disabled:cursor-not-allowed disabled:opacity-40"
            >
              <Send size={16} />
            </button>
          </form>
        </section>
      )}
    </div>
  );
}
