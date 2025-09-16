// src/components/ThumbButton.jsx
import { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { doThumb, undoThumb } from '../store/thumbSlice';
import { formatNumber } from '../utils';

const ThumbButton = ({ blogId, count, hasThumb }) => {
  const dispatch = useDispatch();
  const user = useSelector((state) => state.user.user);
  const [displayCount, setDisplayCount] = useState(count);
  const [isThumbed, setIsThumbed] = useState(hasThumb);
  const [isLoading, setIsLoading] = useState(false);

  // 防抖函数
  const debounce = (func, delay) => {
    let timer;
    return (...args) => {
      clearTimeout(timer);
      timer = setTimeout(() => func(...args), delay);
    };
  };

  // 点赞/取消点赞操作
  const handleThumbAction = async (blogId, currentStatus) => {
    console.log('点赞操作:', currentStatus ? '取消点赞' : '点赞', '博客ID:', blogId);
    setIsLoading(true);
    
    try {
      if (currentStatus) {
        await dispatch(undoThumb(blogId));
      } else {
        await dispatch(doThumb(blogId));
      }
      setIsThumbed(!currentStatus);
      setDisplayCount(currentStatus ? displayCount - 1 : displayCount + 1);
    } catch (error) {
      console.error('点赞操作失败:', error);
    } finally {
      setIsLoading(false);
    }
  };

  // 防抖处理后的点击函数
  const debouncedThumbAction = debounce(handleThumbAction, 200);

  const handleClick = () => {
    if (!user) {
      console.log('用户未登录，不能点赞');
      return;
    }
    debouncedThumbAction(blogId, isThumbed);
  };

  // 格式化点赞数
  const formattedCount = formatNumber(displayCount);

  // 监听props.count变化
  useEffect(() => {
    setDisplayCount(count);
    setIsThumbed(hasThumb);
  }, [count, hasThumb]);

  return (
    <button
      className={`thumb-button flex items-center gap-1.5 px-3 py-1.5 rounded-full border border-gray-200 bg-white transition-all
        ${isThumbed ? 'bg-primary bg-opacity-5 border-primary text-primary' : ''}
        ${!user ? 'opacity-60 cursor-not-allowed' : 'hover:border-primary hover:border-opacity-50'}
      `}
      disabled={!user || isLoading}
      onClick={handleClick}
    >
      <div className="thumb-icon w-5 h-5 relative">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          viewBox="0 0 24 24"
          fill="currentColor"
          className={`w-full h-full absolute inset-0 transition-all
            ${isThumbed ? 'text-primary animate-like' : ''}
          `}
        >
          <path d="M7.493 18.75c-.425 0-.82-.236-.975-.632A7.48 7.48 0 016 15.375c0-1.75.599-3.358 1.602-4.634.151-.192.373-.309.6-.397.473-.183.89-.514 1.212-.924a9.042 9.042 0 012.861-2.4c.723-.384 1.35-.956 1.653-1.715a4.498 4.498 0 00.322-1.672V3a.75.75 0 01.75-.75 2.25 2.25 0 012.25 2.25c0 1.152-.26 2.243-.723 3.218-.266.558.107 1.282.725 1.282h3.126c1.026 0 1.945.694 2.054 1.715.045.422.068.85.068 1.285a11.95 11.95 0 01-2.649 7.521c-.388.482-.987.729-1.605.729H14.23c-.483 0-.964-.078-1.423-.23l-3.114-1.04a4.501 4.501 0 00-1.423-.23h-.777zM2.331 10.977a11.969 11.969 0 00-.831 4.398 12 12 0 00.52 3.507c.26.85 1.084 1.368 1.973 1.368H4.9c.445 0 .72-.498.523-.898a8.963 8.963 0 01-.924-3.977c0-1.708.476-3.305 1.302-4.666.245-.403-.028-.959-.5-.959H4.25c-.832 0-1.612.453-1.918 1.227z" />
        </svg>
      </div>
      <div className="thumb-count relative inline-flex justify-center items-center text-sm font-medium overflow-visible min-w-[1.5rem]">
        <span className={`transition-all ${isThumbed ? 'text-primary' : 'text-gray-600'}`}>
          {formattedCount}
        </span>
      </div>
    </button>
  );
};

export default ThumbButton;
