// src/views/NotFound.jsx
import { useNavigate } from 'react-router-dom';

const NotFound = () => {
  const navigate = useNavigate();

  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center">
      <div className="container-custom py-16">
        <div className="text-center py-16">
          <h1 className="text-8xl font-bold text-primary mb-4">404</h1>
          <h2 className="text-2xl font-bold text-gray-800 mb-4">页面未找到</h2>
          <p className="text-gray-600 mb-8">抱歉，您访问的页面不存在或已被删除。</p>
          <button
            onClick={() => navigate('/')}
            className="btn btn-primary mt-6"
          >
            返回首页
          </button>
        </div>
      </div>
    </div>
  );
};

export default NotFound;
