// src/components/common/ApiLoader.jsx
import React from 'react';
import PropTypes from 'prop-types';

const ApiLoader = ({ loading, fullscreen, message = '加载中...' }) => {
  if (!loading) return null;

  return (
    <div className={`api-loader-wrapper flex items-center justify-center bg-white bg-opacity-80 z-50 ${
      fullscreen ? 'fixed inset-0' : ''
    }`}>
      <div className="api-loader flex flex-col items-center p-4">
        <div className="spinner w-10 h-10 border-4 border-primary border-t-transparent rounded-full animate-spin"></div>
        {message && <div className="message mt-2 text-gray-600 font-medium">{message}</div>}
      </div>
    </div>
  );
};

ApiLoader.propTypes = {
  loading: PropTypes.bool,
  fullscreen: PropTypes.bool,
  message: PropTypes.string,
};

export default ApiLoader;
