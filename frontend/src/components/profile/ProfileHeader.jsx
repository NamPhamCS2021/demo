import React from 'react';

const ProfileHeader = ({ user, customer, onToggleInfo }) => {
    const getInitials = () => {
        if (customer?.firstName && customer?.lastName) {
            return `${customer.firstName.charAt(0)}${customer.lastName.charAt(0)}`.toUpperCase();
        }
        return user?.username?.charAt(0).toUpperCase() || 'U';
    };

    const getFullName = () => {
        if (customer?.firstName && customer?.lastName) {
            return `${customer.firstName} ${customer.lastName}`;
        }
        return user?.username || 'User';
    };

    return (
        <div className="row justify-content-center mb-5">
            <div className="col-lg-10">
                <div className="card border-0 text-white position-relative overflow-hidden shadow-lg"
                     style={{
                         background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                         borderRadius: '20px',
                         minHeight: '300px'
                     }}>

                    {/* Decorative background pattern */}
                    <div className="position-absolute top-0 start-0 w-100 h-100"
                         style={{
                             background: `url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.1'%3E%3Ccircle cx='30' cy='30' r='1'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E")`,
                             opacity: 0.3
                         }} />

                    <div className="card-body d-flex flex-column justify-content-center align-items-center text-center position-relative py-5">
                        {/* Profile Avatar */}
                        <div className="mb-4">
                            <div className="d-flex align-items-center justify-content-center text-white fw-bold shadow-lg"
                                 style={{
                                     width: '100px',
                                     height: '100px',
                                     background: 'rgba(255, 255, 255, 0.2)',
                                     borderRadius: '50%',
                                     fontSize: '2.5rem',
                                     backdropFilter: 'blur(10px)',
                                     border: '3px solid rgba(255, 255, 255, 0.3)'
                                 }}>
                                {getInitials()}
                            </div>
                        </div>

                        {/* Profile Info */}
                        <h1 className="fw-bold mb-2" style={{ fontSize: '2.5rem' }}>
                            {getFullName()}
                        </h1>
                        <p className="mb-3 opacity-90" style={{ fontSize: '1.1rem' }}>
                            {customer?.email || user?.username}
                        </p>

                        <div className="mb-4">
                            <span className="badge bg-light text-primary px-4 py-2 rounded-pill fw-bold">
                                <i className="fas fa-user me-2" />
                                {customer?.type || 'USER'}
                            </span>
                        </div>

                        {/* Action Buttons */}
                        <div className="d-flex gap-3 flex-wrap justify-content-center">
                            <button className="btn btn-light btn-lg rounded-pill px-4 shadow-sm"
                                    onClick={onToggleInfo}>
                                <i className="fas fa-info-circle me-2" />
                                Profile Info
                            </button>
                            <button className="btn btn-outline-light btn-lg rounded-pill px-4">
                                <i className="fas fa-edit me-2" />
                                Edit Profile
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default ProfileHeader;