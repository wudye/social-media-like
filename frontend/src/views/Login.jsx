// src/views/Login.jsx
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { login } from '../store/userSlice';

const Login = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const { isLoading, error: loginError } = useSelector((state) => state.user);
  const [userId, setUserId] = useState('');
  const [error, setError] = useState('');

  // 处理登录
  const handleLogin = async () => {
    // 清除错误信息
    setError('');

    // 表单验证
    if (!userId) {
      setError('input user ID');
      return;
    }

    try {
      console.log('正在尝试登录，用户ID:', userId);
      const success = await dispatch(login(userId)).unwrap();
      console.log('登录结果:', success);
      if (success) {
        // 登录成功，跳转到首页
        navigate('/');
      } else {
        setError('登录失败，请稍后重试');
      }
    } catch (err) {
      console.error('登录异常', err);
      setError('登录异常，请稍后重试');
    }
  };

  // 返回上一页
  const goBack = () => {
    navigate(-1);
  };

  return (
    <div className="min-h-screen bg-gray-50 flex items-center">
      <div className="container-custom py-16">
        <div className="login-card max-w-md mx-auto p-8 bg-white rounded-lg shadow">
          <h1 className="text-2xl font-bold text-gray-800 mb-6 text-center">user login</h1>

          <div className="space-y-6">
            <div className="space-y-2">
              <label htmlFor="userId" className="block text-gray-700 font-medium">login</label>
              <input
                value={userId}
                onChange={(e) => setUserId(e.target.value)}
                type="text"
                id="userId"
                className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent"
                placeholder="input user ID"
                onKeyUp={(e) => e.key === 'Enter' && handleLogin()}
              />
            </div>

            <div className="flex justify-between items-center gap-4 pt-2">
              <button
                onClick={goBack}
                className="btn btn-outline"
              >
              return
              </button>
              <button
                onClick={handleLogin}
                className="btn btn-primary"
                disabled={!userId || isLoading}
              >
                {isLoading ? 'logining...' : 'login'}
              </button>
            </div>

            {error && (
              <div className="text-danger text-sm mt-2">
                {error}
              </div>
            )}

            <div className="text-gray-500 text-sm text-center mt-6">
              <p>提示：输入任意数字ID进行登录</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Login;
