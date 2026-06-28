import {SocketContext} from '@shared/lib/socket/SocketContext';
import {useContext, useEffect} from 'react';

export const useSocket = () => {
  const context = useContext(SocketContext);

  if (!context) {
    throw new Error('useSocket must be used within a StompProvider');
  }

  return context;
};

export const useSocketSubscription = (destination, callback, headers) => {
  const {subscribe, connected} = useSocket();

  useEffect(() => {
    if (!connected || !destination) return;

    const subscription = subscribe(destination, callback, headers);

    return () => {
      subscription?.unsubscribe();
    };
  }, [connected, destination, subscribe, callback, headers]);
};
