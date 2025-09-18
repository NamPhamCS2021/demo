import React from 'react';
import { formatDateTime } from '../../utils/formatters';

const ProfileInformation = ({ user, customer, isVisible }) => {
    if (!isVisible) return null;

    const profileData = [
        { label: 'Username', value: user?.username },
        { label: 'First Name', value: customer?.firstName },
        { label: 'Last Name', value: customer?.lastName },
        { label: 'Email', value: customer?.email },
        { label: 'Phone Number', value: customer?.phoneNumber },
        {
            label: 'Customer Type',
            value: customer?.type,
            isSpecial: true,
            render: (value) => (
                <span className="badge bg-info bg-opacity-10 text-info border border-info border-opacity-25 px-3 py-2 rounded-pill">
                    {value || 'Not specified'}
                </span>
            )
        },
        {
            label: 'Customer Since',
            value: customer?.createdAt ? formatDateTime(customer.createdAt) : null
        },
        {
            label: 'Last Updated',
            value: customer?.updatedAt ? formatDateTime(customer.updatedAt) : null
        },
    ];

    return (
        <div className="mb-5 animate__animated animate__fadeIn">
            <div className="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h3 className="fw-bold text-dark mb-1 d-flex align-items-center">
                        <i className="fas fa-user-circle me-3 text-primary" />
                        Profile Information
                    </h3>
                    <p className="text-muted mb-0">Your personal details and account information</p>
                </div>
            </div>

            <div className="card border-0 shadow-sm" style={{ borderRadius: '16px' }}>
                <div className="card-body p-4">
                    <div className="row">
                        {profileData.map((item, index) => (
                            <div key={index} className="col-md-6 mb-4">
                                <div className="d-flex flex-column">
                                    <label className="text-muted fw-medium mb-2" style={{ fontSize: '0.875rem' }}>
                                        {item.label}
                                    </label>
                                    <div className="fw-medium text-dark">
                                        {item.isSpecial && item.render ?
                                            item.render(item.value) :
                                            (item.value || <span className="text-muted fst-italic">Not provided</span>)
                                        }
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default ProfileInformation;