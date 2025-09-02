import React from 'react';

function CancelButton({
                          onClick,
                          children = 'Cancel',
                          style = {},
                          'aria-label': ariaLabel,
                          ...props
                      }) {
    const defaultStyle = {
        backgroundColor: '#ccc',
        color: '#000',
        padding: '10px 20px',
        border: 'none',
        borderRadius: '4px',
        cursor: 'pointer',
        marginLeft: '10px',
    };

    return (
        <button
            type="button"
            onClick={onClick}
            style={{ ...defaultStyle, ...style }}
            aria-label={ariaLabel || children}
            {...props}
        >
            {children}
        </button>
    );
}

export default CancelButton;