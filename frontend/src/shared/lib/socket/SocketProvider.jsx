import {Client} from '@stomp/stompjs';
import {useCallback, useEffect, useRef, useState,} from 'react';
import SockJS from 'sockjs-client/dist/sockjs';
import {useSnackbar} from '../snackbar/hooks';
import {SocketContext} from './SocketContext';

export const SocketProvider = ({children}) => {
  const clientRef = useRef(null);
  const [connected, setConnected] = useState(false);
  const {showError} = useSnackbar();

  useEffect(() => {
    const client = new Client({
      webSocketFactory: () => new SockJS('/ws'),
      reconnectDelay: 5000,
      onConnect: () => {
        setConnected(true);
      },
      onDisconnect: () => {
        setConnected(false);
      },
      onStompError: (frame) => {
        showError(`STOMP error: ${frame.headers['message'] || 'Unknown error'}`);
      },
      onWebSocketError: (event) => {
        showError('WebSocket connection error');
        console.error('WebSocket error', event);
      },
    });

    clientRef.current = client;
    client.activate();

    return () => {
      client.deactivate().then(() => {
        clientRef.current = null;
        setConnected(false);
      });
    };
  }, []);

  const subscribe = useCallback((destination, callback, headers) => {
    return clientRef.current?.subscribe(destination, callback, headers);
  }, []);

  const send = useCallback((destination, body, headers) => new Promise((resolve, reject) => {
    const client = clientRef.current;

    if (!client) {
      reject(new Error('Client not initialized'));
      return;
    }

    try {
      client.publish({destination, body, headers});
      resolve();
    } catch (e) {
      reject(e);
    }
  }), []);

  const disconnect = useCallback(() => {
    clientRef.current?.deactivate().then(() => setConnected(false));
  }, []);

  const value = {
    connected,
    subscribe,
    send,
    disconnect,
  };

  return (
    <SocketContext value={value}>
      {children}
    </SocketContext>
  );
};
