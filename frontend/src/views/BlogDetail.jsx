// src/views/BlogDetail.jsx
import { useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useSelector, useDispatch } from 'react-redux';
import { fetchBlogDetail } from '../store/blogSlice';
import { restoreLogin } from '../store/userSlice';
import ThumbButton from '../components/ThumbButton';
import LoginBadge from '../components/common/LoginBadge';
import ApiLoader from '../components/common/ApiLoader';
import { formatDate } from '../utils';

const BlogDetail = () => {
  const navigate = useNavigate();
  const { id } = useParams();
  const dispatch = useDispatch();
  const { loading, blog } = useSelector((state) => state.blog);
  const { isLoggedIn } = useSelector((state) => state.user);

  // 格式化时间
  const formattedTime = blog ? formatDate(blog.createTime) : '';

  // 获取博客数据
  const fetchBlogData = async () => {
    await dispatch(fetchBlogDetail(id));
  };

  // 返回上一页
  const goBack = () => {
    navigate(-1);
  };

  // 初始化
  useEffect(() => {
    const initialize = async () => {
      await dispatch(restoreLogin());
      await fetchBlogData();
    };
    initialize();
  }, [id]);

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="container-custom py-8">
        <div className="page-header flex justify-between items-center mb-8">
          <button onClick={goBack} className="back-button flex items-center text-gray-600 hover:text-primary transition-colors">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
              strokeWidth="1.5"
              stroke="currentColor"
              className="back-icon w-5 h-5 mr-1"
            >
              <path strokeLinecap="round" strokeLinejoin="round" d="M10.5 19.5L3 12m0 0l7.5-7.5M3 12h18" />
            </svg>
            return
          </button>
          <LoginBadge />
        </div>

        <ApiLoader loading={loading} fullscreen={true} />

        {blog ? (
          <div className="blog-container card p-6 mb-8">
            <div className="blog-header mb-6">
              <h1 className="blog-title text-3xl font-bold text-gray-800 mb-4">{blog.title}</h1>
              <div className="blog-meta flex justify-between items-center">
                <div className="blog-time text-sm text-gray-500">{formattedTime}</div>
                <ThumbButton blogId={blog.id} count={blog.thumbCount} hasThumb={blog.hasThumb} />
              </div>
            </div>

            <div className="blog-cover mb-6 overflow-hidden rounded-lg">
              <img src={blog.coverImg} alt={blog.title} className="cover-img w-full object-contain max-h-[500px]" referrerPolicy="no-referrer" />
            </div>

            <div className="blog-content text-gray-700 leading-relaxed whitespace-pre-line">
              <p>{blog.content}</p>
            </div>
          </div>
        ) : !loading ? (
          <div className="empty-state flex items-center justify-center py-16 text-gray-500 text-lg">
            <p>blog doesnot exit or deleted</p>
          </div>
        ) : null}
      </div>
    </div>
  );
};

export default BlogDetail;
