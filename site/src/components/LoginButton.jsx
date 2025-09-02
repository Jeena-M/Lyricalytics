import React from 'react';

const buttonStyle = {
    backgroundColor: '#3F51B5',
    color: '#fff',
    padding: '10px 20px',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
};

function LoginButton({
                         onClick,
                         children = 'Login',
                         style = {},
                         'aria-label': ariaLabel,
                         ...props
                     }) {
    return (
        <button
            type="submit"
            onClick={onClick}
            style={{ ...buttonStyle, ...style }}
            aria-label={ariaLabel || children}
            {...props}
        >
            {children}
        </button>
    );
}

export default LoginButton;