package com.nd.gaea.rest.exceptions.support;

import com.nd.gaea.rest.exceptions.extendExceptions.WafSimpleException;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Array;
import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

class WafArrayQueue<E> extends AbstractQueue<E> implements Queue<E>,
		java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -123590092353622781L;

	/** The queued items */
	Object[] items;

	/** items index for next take, poll, peek or remove */
	int takeIndex;

	/** items index for next put, offer, or add */
	int putIndex;

	/** Number of elements in the queue */
	int count;

	/** Main lock guarding all access */
	final ReentrantLock lock;

	public WafArrayQueue(int capacity) {
		this(capacity, false);
	}

	public WafArrayQueue(int capacity, boolean fair) {
		if (capacity <= 0)
			throw new IllegalArgumentException();
		this.items = new Object[capacity];
		lock = new ReentrantLock(fair);
	}

	public boolean add(E e) {
		checkNotNull(e);
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			if (count == items.length) {
				// 说明容器已满，删除最旧的一条
				poll();
				drainTo();
				takeIndex = 0;
				items[putIndex] = e;
			}else {
				items[putIndex] = e;
				putIndex = inc(putIndex);
			}
			++count;
			return true;
		} finally {
			lock.unlock();
		}
	}
	

	public void drainTo() {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			final Object[] items = this.items;
			for (int i = 0; i < items.length-1; i++) {
				items[i] = items[i+1];
			}
			putIndex = items.length-1;
			items[putIndex]=null;
		} finally {
			lock.unlock();
		}
	}
	
	@Override
	public E poll() {
		 final ReentrantLock lock = this.lock;
	        lock.lock();
	        try {
	            return (count == 0) ? null : extract();
	        } finally {
	            lock.unlock();
	        }
	}
	
    private E extract() {
        final Object[] items = this.items;
        E x = cast(items[takeIndex]);
        items[takeIndex] = null;
        takeIndex = inc(takeIndex);
        --count;
        return x;
    }
	
    
    
    public void clear() {
        final Object[] items = this.items;
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            for (int i = takeIndex, k = count; k > 0; i = inc(i), k--)
                items[i] = null;
            count = 0;
            putIndex = 0;
            takeIndex = 0;
        } finally {
            lock.unlock();
        }
    }
   
	/**
	 * Returns the number of elements in this queue.
	 *
	 * @return the number of elements in this queue
	 */
	public int size() {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			return count;
		} finally {
			lock.unlock();
		}
	}
	
    @SuppressWarnings("unchecked")
	public <T> T[] toArray(Class<T> componentType){
    	final ReentrantLock lock = this.lock;
		lock.lock();
          try {
              int size = count>0?count:0;
              Class<?> classType = Class.forName(componentType.getName());
              T [] a = (T[])Array.newInstance(classType, size);
        	  int k = 0;
              for (int i = 0;i<count;i++){
            	  a[k++] = cast(items[i]);
              }
              return a;
          }catch(ClassNotFoundException e){
        	  e.printStackTrace();
        	  throw new WafSimpleException(HttpStatus.INTERNAL_SERVER_ERROR, "WAF/CLASS_NOT_FOUND_ERROR", "WafArrayBlockingQueue "+componentType+" not found");
          }finally {
        	  lock.unlock();
          }
    }
    
    /**
     * 
    * @Title: copyArray 
    * @Description: 重置异常容器的长度
    * @param @param maxEntries    设定文件 
    * @return void    返回类型 
    * @throws
     */
	public void copyArray(int maxEntries) {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			if (maxEntries>0) {
				Object[] defaultItems = this.items;
				Object[] tempItems = new Object[maxEntries];
				int j = 0;
				if (count>0) {
					for (int i = defaultItems.length-1 ; i >=0 && j<tempItems.length; i--) {
						if (defaultItems[i]!=null) {
							tempItems[j++] = cast(defaultItems[i]);
						}
					}
				}
				count = j;
				putIndex = j<maxEntries?j:(j-1)>0?(j-1):0;
		        takeIndex = 0;
		        this.items = new Object[maxEntries];
		        if (count>0) {
		        	int k = 0;
		        	for (int i = tempItems.length-1; i >=0; i--) {
		        		if (tempItems[i]!=null) {
		        			items[k++] = tempItems[i];	
						}
					}
				}
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Circularly increment i.
	 */
	final int inc(int i) {
		return (++i == items.length) ? 0 : i;
	}

	/**
	 * Circularly decrement i.
	 */
	final int dec(int i) {
		return ((i == 0) ? items.length : i) - 1;
	}
	
    @SuppressWarnings("unchecked")
    static <E> E cast(Object item) {
        return (E) item;
    }

	/**
	 * 添加一个元素并返回true 如果队列已满，则返回false
	 */
	@Override
	public boolean offer(E e) {
		return false;
	}

	@Override
	public E peek() {
		return null;
	}

	@Override
	public Iterator<E> iterator() {
		return null;
	}

	/**
	 * Throws NullPointerException if argument is null.
	 *
	 * @param v
	 *            the element
	 */
	private static void checkNotNull(Object v) {
		if (v == null)
			throw new NullPointerException();
	}
}
