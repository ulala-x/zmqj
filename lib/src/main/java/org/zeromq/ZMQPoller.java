package org.zeromq;

import java.nio.channels.SelectableChannel;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public  class ZMQPoller {

    static {
        if (!EmbeddedLibraryTools.LOADED_EMBEDDED_LIBRARY){
            System.loadLibrary("libzmqj");
        }
    }


    public static class Item {
        private ZMQSocket socket;
        private SelectableChannel channel;
        private int events;
        private int revents;


        public Item(ZMQSocket socket, int events) {
            this.socket = socket;
            this.events = events;
            this.revents = 0;
        }

        public Item(SelectableChannel channel, int events) {
            this.channel = channel;
            this.events = events;
            this.revents = 0;
        }

        public SelectableChannel getRawSocket() {
            return channel;
        }

        public ZMQSocket getSocket() {
            return socket;
        }

        public boolean isError() {
            return (revents & PollEvent.POLLERR.value()) > 0;
        }

        public int readyOps() {
            return revents;
        }

        public boolean isReadable() {
            return (revents & PollEvent.POLLIN.value()) > 0;
        }

        public boolean isWritable() {
            return (revents & PollEvent.POLLOUT.value()) > 0;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Item))
                return false;

            Item target = (Item) obj;
            if (socket != null && socket == target.socket)
                return true;

            if (channel != null && channel == target.channel)
                return true;

            return false;
        }
    }

    /**
     * These values can be ORed to specify what we want to poll for.
     */
//    public static final int POLLIN = 1;
//    public static final int POLLOUT = 2;
//    public static final int POLLERR = 4;

    /**
     * Register a Socket for polling on all events.
     *
     * @param socket the Socket we are registering.
     * @return the index identifying this Socket in the poll set.
     */
    public int register(ZMQSocket socket) {
        return register(socket, PollEvent.ALL);
    }

    /**
     * Register a Channel for polling on all events.
     *
     * @param channel the Channel we are registering.
     * @return the index identifying this Channel in the poll set.
     */
    public int register(SelectableChannel channel) {
        return register(channel, PollEvent.ALL.value());
    }

    /**
     * Register a Socket for polling on the specified events.
     *
     * Automatically grow the internal representation if needed.
     *
     * @param socket the Socket we are registering.
     * @param events a mask composed by XORing POLLIN, POLLOUT and POLLERR.
     * @return the index identifying this Socket in the poll set.
     */
    public int register(ZMQSocket socket, PollEvent events) {
        return registerInternal(new Item(socket, events.value()));
    }

    /**
     * Register a Channel for polling on the specified events.
     *
     * Automatically grow the internal representation if needed.
     *
     * @param channel the Channel we are registering.
     * @param events a mask composed by XORing POLLIN, POLLOUT and POLLERR.
     * @return the index identifying this Channel in the poll set.
     */
    public int register(SelectableChannel channel, int events) {
        return registerInternal(new Item(channel, events));
    }

    /**
     * Register a Channel for polling on the specified events.
     *
     * Automatically grow the internal representation if needed.
     *
     * @param item the PollItem we are registering.
     * @return the index identifying this Channel in the poll set.
     */
    public int register(Item item) {
        return registerInternal(item);
    }

    /**
     * Register a Socket for polling on the specified events.
     *
     * Automatically grow the internal representation if needed.
     *
     * @param item the PollItem we are registering.
     * @return the index identifying this Socket in the poll set.
     */
    private int registerInternal(Item item) {
        int pos = -1;

        if (!this.freeSlots.isEmpty()) {
            // If there are free slots in our array, remove one
            // from the free list and use it.
            pos = this.freeSlots.remove();
        } else {
            if (this.next >= this.size) {
                // It is necessary to grow the arrays.

                // Compute new size for internal arrays.
                int nsize = this.size + SIZE_INCREMENT;

                // Create new internal arrays.
                Item[] ns = new Item[nsize];
                short[] ne = new short[nsize];
                short[] nr = new short[nsize];

                // Copy contents of current arrays into new arrays.
                for (int i = 0; i < this.next; ++i) {
                    ns[i] = this.items[i];
                }

                // Swap internal arrays and size to new values.
                this.size = nsize;
                this.items = ns;
            }
            pos = this.next++;
        }

        this.items[pos] = item;
        this.used++;
        return pos;
    }

    /**
     * Unregister a Socket for polling on the specified events.
     *
     * @param socket the Socket to be unregistered
     */
    public void unregister(ZMQSocket socket) {
        unregisterInternal(socket);
    }

    /**
     * Unregister a Channel for polling on the specified events.
     *
     * @param channel the Channel to be unregistered
     */
    public void unregister(SelectableChannel channel) {
        unregisterInternal(channel);
    }

    /**
     * Unregister a Socket for polling on the specified events.
     *
     * @param socket the Socket to be unregistered
     */
    private void unregisterInternal(Object socket) {
        for (int i = 0; i < this.next; ++i) {
            Item item = this.items[i];
            if (item == null) {
                continue;
            }
            if (item.socket == socket || item.channel == socket) {
                this.items[i] = null;

                this.freeSlots.add(i);
                --this.used;

                break;
            }
        }
    }

    /**
     * Get the socket associated with an index.
     *
     * @param index the desired index.
     * @return the Socket associated with that index (or null).
     */
    public ZMQSocket getSocket(int index) {
        if (index < 0 || index >= this.next)
            return null;
        return this.items[index].socket;
    }

    /**
     * Get the PollItem associated with an index.
     *
     * @param index the desired index.
     * @return the PollItem associated with that index (or null).
     */
    public Item getItem(int index) {
        if (index < 0 || index >= this.next)
            return null;
        return this.items[index];
    }

//    /**
//     * Get the current poll timeout.
//     *
//     * @return the current poll timeout in microseconds.
//     * @deprecated Timeout handling has been moved to the poll() methods.
//     */
//    public long getTimeout() {
//        return this.timeout;
//    }
//
//    /**
//     * Set the poll timeout.
//     *
//     * @param timeout the desired poll timeout in microseconds.
//     * @deprecated Timeout handling has been moved to the poll() methods.
//     */
//    public void setTimeout(long timeout) {
//        if (timeout < -1)
//            return;
//
//        this.timeout = timeout;
//    }

    /**
     * Get the current poll set size.
     *
     * @return the current poll set size.
     */
    public int getSize() {
        return this.size;
    }

    /**
     * Get the index for the next position in the poll set size.
     *
     * @return the index for the next position in the poll set size.
     */
    public int getNext() {
        return this.next;
    }

    /**
     * Issue a poll call. If the poller's internal timeout value has been set, use that value as timeout; otherwise,
     * block indefinitely.
     *
     * @return how many objects where signalled by poll ().
     */
    public long poll() {
        long tout = -1;
        if (this.timeout > -1) {
            tout = this.timeout;
        }
        return poll(tout);
    }

    /**
     * Issue a poll call, using the specified timeout value.
     * <p>
     * Since ZeroMQ 3.0, the timeout parameter is in <i>milliseconds</i>,
     * but prior to this the unit was <i>microseconds</i>.
     *
     * @param tout the timeout, as per zmq_poll if -1, it will block
     *             indefinitely until an event happens; if 0, it will
     *             return immediately; otherwise, it will wait for at most
     *             that many milliseconds/microseconds (see above).
     *
     * @see <a href="http://api.zeromq.org/2-1:zmq-poll">2.1 docs</a>
     * @see <a href="http://api.zeromq.org/3-0:zmq-poll">3.0 docs</a>
     *
     * @return how many objects where signalled by poll ()
     */
    public int poll(long tout) {
        if (tout < -1) {
            return 0;
        }
        if (this.size <= 0 || this.next <= 0) {
            return 0;
        }

        return run_poll(this.items, this.used, tout);
    }

    /**
     * Check whether the specified element in the poll set was signalled for input.
     *
     * @param index
     *
     * @return true if the element was signalled.
     */
    public boolean pollin(int index) {
        return poll_mask(index, PollEvent.POLLIN.value());
    }

    /**
     * Check whether the specified element in the poll set was signalled for output.
     *
     * @param index
     *
     * @return true if the element was signalled.
     */
    public boolean pollout(int index) {
        return poll_mask(index, PollEvent.POLLOUT.value());
    }

    /**
     * Check whether the specified element in the poll set was signalled for error.
     *
     * @param index
     *
     * @return true if the element was signalled.
     */
    public boolean pollerr(int index) {
        return poll_mask(index, PollEvent.POLLERR.value());
    }

    /**
     * Constructor
     *
     * @param size the number of Sockets this poller will contain.
     */
    public ZMQPoller(int size) {
        this(null, size);
    }

    /**
     * Class constructor.
     *
     * @param context a 0MQ context previously created.
     */
    protected ZMQPoller(ZMQContext context) {
        this(context, SIZE_DEFAULT);
    }

    /**
     * Class constructor.
     *
     * @param context a 0MQ context previously created.
     * @param size the number of Sockets this poller will contain.
     */
    protected ZMQPoller(ZMQContext context, int size) {
        this.context = context;
        this.size = size;
        this.next = 0;

        this.items = new Item[this.size];

        freeSlots = new LinkedList<Integer>();
    }

    /**
     * Issue a poll call on the specified 0MQ items.
     * <p>
     * Since ZeroMQ 3.0, the timeout parameter is in <i>milliseconds</i>, but prior to this the unit was
     * <i>microseconds</i>.
     *
     * @param items an array of PollItem to poll.
     * @param timeout the maximum timeout in milliseconds/microseconds (see above).
     * @return how many objects where signalled by poll.
     * @see <a href="http://api.zeromq.org/2-1:zmq-poll">2.1 docs</a>
     * @see <a href="http://api.zeromq.org/3-0:zmq-poll">3.0 docs</a>
     */
    private native static int _run_poll(Item[] items, int count, long timeout);
    protected static int run_poll(Item[] items, int count, long timeout){
        return _run_poll(items,count,timeout);
    }

    /**
     * Check whether a specific mask was signalled by latest poll call.
     *
     * @param index the index indicating the socket.
     * @param mask a combination of POLLIN, POLLOUT and POLLERR.
     * @return true if specific socket was signalled as specified.
     */
    private boolean poll_mask(int index, int mask) {
        if (mask <= 0 || index < 0 || index >= this.next || this.items[index] == null) {
            return false;
        }
        return (this.items[index].revents & mask) > 0;
    }

    private ZMQContext context = null;
    private long timeout = -2; // mark as uninitialized
    private int size = 0;
    private int next = 0;
    private int used = 0;
    private Item[] items = null;
    // When socket is removed from polling, store free slots here
    private LinkedList<Integer> freeSlots = null;

    private static final int SIZE_DEFAULT = 32;
    private static final int SIZE_INCREMENT = 16;
}