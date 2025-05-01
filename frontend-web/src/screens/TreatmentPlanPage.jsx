import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { 
  Box, Container, Typography, Button, CircularProgress, Alert, Grid, Card,
  TextField, Dialog, DialogTitle, DialogContent, DialogActions, FormControl,
  InputLabel, Select, MenuItem, FormHelperText, Snackbar
} from '@mui/material';
import { Add as AddIcon, Edit as EditIcon, Delete as DeleteIcon } from '@mui/icons-material';
import UserNavBar from '../components/UserNavBar';
import AuthService from '../config/AuthService';

const TreatmentPlanPage = () => {
    const [treatmentPlans, setTreatmentPlans] = useState([]);
    const [pets, setPets] = useState([]);
    const [selectedPetId, setSelectedPetId] = useState('');
    const [error, setError] = useState(null);
    const [isLoading, setIsLoading] = useState(false);
    const [modalOpen, setModalOpen] = useState(false);
    const [currentPlan, setCurrentPlan] = useState({
        name: '',
        description: '',
        goal: '',
        startDate: '',
        endDate: '',
        status: 'ACTIVE',
        progressPercentage: 0,
        notes: ''
    });
    const [isEditing, setIsEditing] = useState(false);
    const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' });

    const colors = {
      yellow: "#F0B542",
      darkBlue: "#042C3C",
      coral: "#EA6C7B",
      cream: "#FFF7EC",
    };
    
    // Get current user ID from auth service
    const userID = AuthService.getCurrentUser()?.id;
    const token = localStorage.getItem('jwtToken');
    
    // Fetch user's pets
    useEffect(() => {
        const fetchPets = async () => {
            if (!userID || !token) {
                setError('You must be logged in to view your pets');
                return;
            }
            
            try {
                const response = await axios.get(`/api/users/${userID}/pets`, {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                });
                
                if (Array.isArray(response.data)) {
                    setPets(response.data);
                    // Auto-select first pet if available
                    if (response.data.length > 0 && !selectedPetId) {
                        setSelectedPetId(response.data[0].id);
                    }
                } else {
                    setPets([]);
                }
            } catch (err) {
                console.error('Error fetching pets:', err);
                if (err.response && err.response.status === 403) {
                    setError('Session expired. Please log in again.');
                    AuthService.logout(); // Clear invalid token
                } else {
                    setError('Failed to load your pets. Please try again.');
                }
            }
        };
        
        fetchPets();
    }, [userID, token]);
    
    // Fetch treatment plans when pet selection changes
    useEffect(() => {
        if (!selectedPetId) return;
        
        const fetchTreatmentPlans = async () => {
            setIsLoading(true);
            try {
                const response = await axios.get(`/api/users/${userID}/pets/${selectedPetId}/treatmentPlans`, {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                });
                
                if (Array.isArray(response.data)) {
                    setTreatmentPlans(response.data);
                } else {
                    setTreatmentPlans([]);
                }
                setError(null);
            } catch (err) {
                console.error('Error fetching treatment plans:', err);
                if (err.response) {
                    if (err.response.status === 403) {
                        setError('Session expired. Please log in again.');
                        AuthService.logout(); // Clear invalid token
                    } else {
                        setError('Failed to load treatment plans. Please try again.');
                    }
                } else {
                    setError('Network error. Please check your connection.');
                }
                setTreatmentPlans([]);
            } finally {
                setIsLoading(false);
            }
        };

        fetchTreatmentPlans();
    }, [selectedPetId, userID, token]);

    const handlePetChange = (event) => {
        setSelectedPetId(event.target.value);
    };

    const openCreateModal = () => {
        setCurrentPlan({
            name: '',
            description: '',
            goal: '',
            startDate: formatDate(new Date()),
            endDate: '',
            status: 'ACTIVE',
            progressPercentage: 0,
            notes: ''
        });
        setIsEditing(false);
        setModalOpen(true);
    };

    const openEditModal = (plan) => {
        setCurrentPlan({
            ...plan,
            startDate: formatDate(new Date(plan.startDate)),
            endDate: plan.endDate ? formatDate(new Date(plan.endDate)) : ''
        });
        setIsEditing(true);
        setModalOpen(true);
    };

    const closeModal = () => {
        setModalOpen(false);
    };

    const formatDate = (date) => {
        return date.toISOString().split('T')[0];
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setCurrentPlan({
            ...currentPlan,
            [name]: value
        });
    };

    const handleSubmit = async () => {
        if (!selectedPetId) {
            setSnackbar({
                open: true,
                message: 'Please select a pet first',
                severity: 'error'
            });
            return;
        }

        try {
            let response;
            if (isEditing) {
                response = await axios.put(
                    `/api/users/${userID}/pets/${selectedPetId}/treatmentPlans/${currentPlan.planID}`,
                    currentPlan,
                    {
                        headers: {
                            Authorization: `Bearer ${token}`
                        }
                    }
                );
                setSnackbar({
                    open: true,
                    message: 'Treatment plan updated successfully',
                    severity: 'success'
                });
            } else {
                response = await axios.post(
                    `/api/users/${userID}/pets/${selectedPetId}/treatmentPlans`,
                    currentPlan,
                    {
                        headers: {
                            Authorization: `Bearer ${token}`
                        }
                    }
                );
                setSnackbar({
                    open: true,
                    message: 'Treatment plan created successfully',
                    severity: 'success'
                });
            }
            
            // Refresh treatment plans
            const updatedPlans = isEditing
                ? treatmentPlans.map(p => p.planID === currentPlan.planID ? response.data : p)
                : [...treatmentPlans, response.data];
            
            setTreatmentPlans(updatedPlans);
            closeModal();
        } catch (err) {
            console.error('Error saving treatment plan:', err);
            setSnackbar({
                open: true,
                message: 'Failed to save treatment plan. Please try again.',
                severity: 'error'
            });
        }
    };

    const handleDelete = async (planID) => {
        if (!window.confirm('Are you sure you want to delete this treatment plan?')) {
            return;
        }
        
        try {
            await axios.delete(`/api/users/${userID}/pets/${selectedPetId}/treatmentPlans/${planID}`, {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });
            
            setTreatmentPlans(treatmentPlans.filter(plan => plan.planID !== planID));
            setSnackbar({
                open: true,
                message: 'Treatment plan deleted successfully',
                severity: 'success'
            });
        } catch (err) {
            console.error('Error deleting treatment plan:', err);
            setSnackbar({
                open: true,
                message: 'Failed to delete treatment plan',
                severity: 'error'
            });
        }
    };

    const handleCloseSnackbar = () => {
        setSnackbar({ ...snackbar, open: false });
    };

    // Check if user is logged in
    if (!userID || !token) {
        return (
            <Box
                sx={{
                    minHeight: "100vh",
                    width: "100%",
                    backgroundColor: colors.cream,
                    display: "flex",
                    flexDirection: "column",
                    alignItems: "center",
                    justifyContent: "center",
                }}
            >
                <Alert severity="warning" sx={{ mb: 2 }}>
                    You must be logged in to view treatment plans
                </Alert>
                <Button
                    variant="contained"
                    onClick={() => window.location.href = '/login'}
                    sx={{
                        backgroundColor: colors.coral,
                        '&:hover': { backgroundColor: colors.coral, opacity: 0.9 }
                    }}
                >
                    Go to Login
                </Button>
            </Box>
        );
    }

    return (
        <Box
            sx={{
                minHeight: "100vh",
                width: "100%",
                backgroundColor: colors.cream,
                position: "relative",
                overflowX: "hidden",
                fontFamily: "'Plus Jakarta Sans', sans-serif",
                display: "flex",
                flexDirection: "column",
            }}
        >
            <UserNavBar />

            {/* Header Section */}
            <Box
                sx={{
                    backgroundColor: colors.darkBlue,
                    padding: "20px 0",
                    position: "relative",
                    overflow: "hidden",
                }}
            >
                <Box
                    sx={{
                        position: "absolute",
                        top: "-50px",
                        right: "-50px",
                        width: "200px",
                        height: "200px",
                        backgroundColor: colors.yellow,
                        borderRadius: "50%",
                        opacity: 0.2,
                    }}
                />
                <Container maxWidth="lg">
                    <Typography
                        variant="h3"
                        sx={{
                            color: "white",
                            fontFamily: "'Baloo 2', cursive",
                            fontWeight: "bold",
                            textAlign: "center",
                            position: "relative",
                            zIndex: 1,
                        }}
                    >
                        Treatment Plans
                    </Typography>
                    <Typography
                        variant="body1"
                        sx={{
                            color: "white",
                            opacity: 0.8,
                            textAlign: "center",
                            maxWidth: "700px",
                            margin: "0 auto",
                            mt: 1,
                            position: "relative",
                            zIndex: 1,
                        }}
                    >
                        Create and manage personalized treatment plans for your pets' health needs
                    </Typography>
                </Container>
            </Box>

            {/* Main Content */}
            <Container maxWidth="lg" sx={{ py: 4, flex: 1 }}>
                {/* Pet Selection Dropdown */}
                <Box sx={{ mb: 4 }}>
                    <FormControl fullWidth variant="outlined" sx={{ mb: 2 }}>
                        <InputLabel id="pet-select-label">Select Pet</InputLabel>
                        <Select
                            labelId="pet-select-label"
                            id="pet-select"
                            value={selectedPetId}
                            onChange={handlePetChange}
                            label="Select Pet"
                            sx={{
                                borderRadius: "8px",
                                backgroundColor: "white",
                                '& .MuiOutlinedInput-notchedOutline': {
                                    borderColor: colors.coral + '40',
                                },
                                '&:hover .MuiOutlinedInput-notchedOutline': {
                                    borderColor: colors.coral,
                                },
                            }}
                        >
                            {pets.length === 0 ? (
                                <MenuItem value="" disabled>No pets available</MenuItem>
                            ) : (
                                pets.map((pet) => (
                                    <MenuItem key={pet.id} value={pet.id}>
                                        {pet.name} ({pet.species})
                                    </MenuItem>
                                ))
                            )}
                        </Select>
                        {pets.length === 0 && (
                            <FormHelperText>
                                You need to add pets before creating treatment plans
                            </FormHelperText>
                        )}
                    </FormControl>
                </Box>

                <Box
                    sx={{
                        display: "flex",
                        justifyContent: "space-between",
                        alignItems: "center",
                        mb: 4,
                        flexWrap: "wrap",
                        gap: 2,
                    }}
                >
                    <Typography
                        variant="h5"
                        sx={{
                            fontFamily: "'Baloo 2', cursive",
                            color: colors.darkBlue,
                            fontWeight: "bold",
                        }}
                    >
                        Your Pet Treatment Plans
                    </Typography>

                    <Button
                        variant="contained"
                        onClick={openCreateModal}
                        disabled={!selectedPetId}
                        startIcon={<AddIcon />}
                        sx={{
                            backgroundColor: colors.coral,
                            color: "white",
                            borderRadius: "9999px",
                            padding: "8px 24px",
                            fontSize: "14px",
                            textTransform: "none",
                            boxShadow: "none",
                            "&:hover": {
                                backgroundColor: colors.coral,
                                opacity: 0.9,
                                boxShadow: "none",
                            },
                            "&:disabled": {
                                backgroundColor: "#ccc",
                                color: "#666",
                            }
                        }}
                    >
                        New Plan
                    </Button>
                </Box>

                {/* Loading and Error States */}
                {isLoading && (
                    <Box sx={{ display: "flex", justifyContent: "center", my: 5 }}>
                        <CircularProgress sx={{ color: colors.coral }} />
                    </Box>
                )}

                {error && (
                    <Alert severity="error" sx={{ mb: 3 }}>
                        {error}
                    </Alert>
                )}

                {/* Treatment Plans Grid */}
                {!isLoading && !error && selectedPetId && (
                    <Grid container spacing={3}>
                        {treatmentPlans.length > 0 ? (
                            treatmentPlans.map((plan) => (
                                <Grid item xs={12} md={6} lg={4} key={plan.planID}>
                                    <Card
                                        sx={{
                                            borderRadius: "16px",
                                            boxShadow: "0 4px 20px rgba(0,0,0,0.08)",
                                            height: "100%",
                                            display: "flex",
                                            flexDirection: "column",
                                            transition: "transform 0.3s ease-in-out, box-shadow 0.3s ease-in-out",
                                            "&:hover": {
                                                transform: "translateY(-5px)",
                                                boxShadow: "0 10px 30px rgba(0,0,0,0.12)",
                                            },
                                            border: `2px solid ${colors.coral}20`,
                                            position: "relative",
                                            overflow: "hidden",
                                        }}
                                    >
                                        {/* Status indicator */}
                                        <Box
                                            sx={{
                                                position: "absolute",
                                                top: 0,
                                                right: 0,
                                                backgroundColor: 
                                                    plan.status === 'ACTIVE' ? colors.yellow :
                                                    plan.status === 'COMPLETED' ? '#4CAF50' : '#F44336',
                                                color: "white",
                                                padding: "4px 12px",
                                                borderBottomLeftRadius: "12px",
                                                fontSize: "12px",
                                                fontWeight: "bold",
                                            }}
                                        >
                                            {plan.status}
                                        </Box>
                                        
                                        <Box sx={{ padding: 3, flexGrow: 1 }}>
                                            <Typography 
                                                variant="h6" 
                                                sx={{ 
                                                    fontWeight: "bold",
                                                    color: colors.darkBlue,
                                                    mb: 1
                                                }}
                                            >
                                                {plan.name}
                                            </Typography>
                                            
                                            <Typography 
                                                variant="body2" 
                                                sx={{ 
                                                    color: "#666",
                                                    mb: 2,
                                                    height: "40px",
                                                    overflow: "hidden",
                                                    textOverflow: "ellipsis",
                                                    display: "-webkit-box",
                                                    WebkitLineClamp: 2,
                                                    WebkitBoxOrient: "vertical",
                                                }}
                                            >
                                                {plan.description}
                                            </Typography>
                                            
                                            <Typography 
                                                variant="body2" 
                                                sx={{ 
                                                    fontWeight: "medium",
                                                    mb: 1
                                                }}
                                            >
                                                <strong>Goal:</strong> {plan.goal}
                                            </Typography>
                                            
                                            <Typography variant="body2" sx={{ mb: 0.5 }}>
                                                <strong>Start:</strong> {new Date(plan.startDate).toLocaleDateString()}
                                            </Typography>
                                            
                                            {plan.endDate && (
                                                <Typography variant="body2" sx={{ mb: 0.5 }}>
                                                    <strong>End:</strong> {new Date(plan.endDate).toLocaleDateString()}
                                                </Typography>
                                            )}
                                            
                                            {/* Progress bar */}
                                            <Box sx={{ mt: 2 }}>
                                                <Typography variant="body2" sx={{ mb: 0.5 }}>
                                                    Progress: {plan.progressPercentage}%
                                                </Typography>
                                                <Box
                                                    sx={{
                                                        height: "8px",
                                                        backgroundColor: "#eee",
                                                        borderRadius: "4px",
                                                        overflow: "hidden",
                                                    }}
                                                >
                                                    <Box
                                                        sx={{
                                                            height: "100%",
                                                            width: `${plan.progressPercentage}%`,
                                                            backgroundColor: colors.coral,
                                                        }}
                                                    />
                                                </Box>
                                            </Box>
                                        </Box>
                                        
                                        {/* Action buttons */}
                                        <Box
                                            sx={{
                                                display: "flex",
                                                justifyContent: "flex-end",
                                                padding: 2,
                                                borderTop: "1px solid #eee",
                                            }}
                                        >
                                            <Button
                                                startIcon={<EditIcon />}
                                                onClick={() => openEditModal(plan)}
                                                sx={{
                                                    color: colors.darkBlue,
                                                    mr: 1,
                                                    "&:hover": {
                                                        backgroundColor: colors.darkBlue + "10",
                                                    },
                                                }}
                                            >
                                                Edit
                                            </Button>
                                            <Button
                                                startIcon={<DeleteIcon />}
                                                onClick={() => handleDelete(plan.planID)}
                                                sx={{
                                                    color: colors.coral,
                                                    "&:hover": {
                                                        backgroundColor: colors.coral + "10",
                                                    },
                                                }}
                                            >
                                                Delete
                                            </Button>
                                        </Box>
                                    </Card>
                                </Grid>
                            ))
                        ) : (
                            <Box sx={{ width: "100%", textAlign: "center", py: 5 }}>
                                <Typography variant="body1" sx={{ color: "#666", mb: 2 }}>
                                    No treatment plans available for this pet.
                                </Typography>
                                <Button
                                    variant="outlined"
                                    onClick={openCreateModal}
                                    sx={{
                                        borderColor: colors.coral,
                                        color: colors.coral,
                                        "&:hover": {
                                            borderColor: colors.coral,
                                            backgroundColor: colors.coral + "10",
                                        },
                                    }}
                                >
                                    Create Your First Treatment Plan
                                </Button>
                            </Box>
                        )}
                    </Grid>
                )}
            </Container>

            {/* Treatment Plan Modal */}
            <Dialog 
                open={modalOpen} 
                onClose={closeModal}
                maxWidth="md"
                fullWidth
                PaperProps={{
                    sx: {
                        borderRadius: "16px",
                        padding: 2,
                    }
                }}
            >
                <DialogTitle sx={{ 
                    fontFamily: "'Baloo 2', cursive",
                    color: colors.darkBlue,
                    fontWeight: "bold",
                    fontSize: "1.5rem"
                }}>
                    {isEditing ? 'Edit Treatment Plan' : 'Create New Treatment Plan'}
                </DialogTitle>
                
                <DialogContent>
                    <Grid container spacing={2} sx={{ mt: 1 }}>
                        <Grid item xs={12}>
                            <TextField
                                name="name"
                                label="Plan Name"
                                value={currentPlan.name}
                                onChange={handleInputChange}
                                fullWidth
                                required
                                variant="outlined"
                                sx={{
                                    '& .MuiOutlinedInput-root': {
                                        '& fieldset': {
                                            borderColor: colors.coral + '40',
                                        },
                                        '&:hover fieldset': {
                                            borderColor: colors.coral,
                                        },
                                    },
                                }}
                            />
                        </Grid>
                        
                        <Grid item xs={12}>
                            <TextField
                                name="description"
                                label="Description"
                                value={currentPlan.description}
                                onChange={handleInputChange}
                                fullWidth
                                multiline
                                rows={2}
                                variant="outlined"
                                sx={{
                                    '& .MuiOutlinedInput-root': {
                                        '& fieldset': {
                                            borderColor: colors.coral + '40',
                                        },
                                        '&:hover fieldset': {
                                            borderColor: colors.coral,
                                        },
                                    },
                                }}
                            />
                        </Grid>
                        
                        <Grid item xs={12}>
                            <TextField
                                name="goal"
                                label="Treatment Goal"
                                value={currentPlan.goal}
                                onChange={handleInputChange}
                                fullWidth
                                required
                                variant="outlined"
                                sx={{
                                    '& .MuiOutlinedInput-root': {
                                        '& fieldset': {
                                            borderColor: colors.coral + '40',
                                        },
                                        '&:hover fieldset': {
                                            borderColor: colors.coral,
                                        },
                                    },
                                }}
                            />
                        </Grid>
                        
                        <Grid item xs={12} sm={6}>
                            <TextField
                                name="startDate"
                                label="Start Date"
                                type="date"
                                value={currentPlan.startDate}
                                onChange={handleInputChange}
                                fullWidth
                                required
                                InputLabelProps={{ shrink: true }}
                                variant="outlined"
                                sx={{
                                    '& .MuiOutlinedInput-root': {
                                        '& fieldset': {
                                            borderColor: colors.coral + '40',
                                        },
                                        '&:hover fieldset': {
                                            borderColor: colors.coral,
                                        },
                                    },
                                }}
                            />
                        </Grid>
                        
                        <Grid item xs={12} sm={6}>
                            <TextField
                                name="endDate"
                                label="End Date"
                                type="date"
                                value={currentPlan.endDate}
                                onChange={handleInputChange}
                                fullWidth
                                InputLabelProps={{ shrink: true }}
                                variant="outlined"
                                sx={{
                                    '& .MuiOutlinedInput-root': {
                                        '& fieldset': {
                                            borderColor: colors.coral + '40',
                                        },
                                        '&:hover fieldset': {
                                            borderColor: colors.coral,
                                        },
                                    },
                                }}
                            />
                        </Grid>
                        
                        {isEditing && (
                            <>
                                <Grid item xs={12} sm={6}>
                                    <FormControl fullWidth variant="outlined">
                                        <InputLabel>Status</InputLabel>
                                        <Select
                                            name="status"
                                            value={currentPlan.status}
                                            onChange={handleInputChange}
                                            label="Status"
                                            sx={{
                                                '& .MuiOutlinedInput-notchedOutline': {
                                                    borderColor: colors.coral + '40',
                                                },
                                                '&:hover .MuiOutlinedInput-notchedOutline': {
                                                    borderColor: colors.coral,
                                                },
                                            }}
                                        >
                                            <MenuItem value="ACTIVE">Active</MenuItem>
                                            <MenuItem value="COMPLETED">Completed</MenuItem>
                                            <MenuItem value="CANCELLED">Cancelled</MenuItem>
                                        </Select>
                                    </FormControl>
                                </Grid>
                                
                                <Grid item xs={12} sm={6}>
                                    <Typography gutterBottom>
                                        Progress: {currentPlan.progressPercentage}%
                                    </Typography>
                                    <TextField
                                        name="progressPercentage"
                                        type="range"
                                        value={currentPlan.progressPercentage}
                                        onChange={handleInputChange}
                                        fullWidth
                                        inputProps={{ min: 0, max: 100, step: 5 }}
                                        sx={{
                                            '& .MuiSlider-thumb': {
                                                color: colors.coral,
                                            },
                                            '& .MuiSlider-track': {
                                                color: colors.coral,
                                            },
                                        }}
                                    />
                                </Grid>
                            </>
                        )}
                        
                        <Grid item xs={12}>
                            <TextField
                                name="notes"
                                label="Additional Notes"
                                value={currentPlan.notes}
                                onChange={handleInputChange}
                                fullWidth
                                multiline
                                rows={3}
                                variant="outlined"
                                sx={{
                                    '& .MuiOutlinedInput-root': {
                                        '& fieldset': {
                                            borderColor: colors.coral + '40',
                                        },
                                        '&:hover fieldset': {
                                            borderColor: colors.coral,
                                        },
                                    },
                                }}
                            />
                        </Grid>
                    </Grid>
                </DialogContent>
                
                <DialogActions sx={{ padding: 3 }}>
                    <Button 
                        onClick={closeModal}
                        sx={{ 
                            color: "#666",
                            '&:hover': {
                                backgroundColor: '#f5f5f5',
                            }
                        }}
                    >
                        Cancel
                    </Button>
                    <Button 
                        onClick={handleSubmit}
                        variant="contained"
                        sx={{
                            backgroundColor: colors.coral,
                            color: "white",
                            '&:hover': {
                                backgroundColor: colors.coral,
                                opacity: 0.9,
                            }
                        }}
                    >
                        {isEditing ? 'Update Plan' : 'Create Plan'}
                    </Button>
                </DialogActions>
            </Dialog>

            {/* Snackbar for notifications */}
            <Snackbar
                open={snackbar.open}
                autoHideDuration={6000}
                onClose={handleCloseSnackbar}
                anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
            >
                <Alert 
                    onClose={handleCloseSnackbar} 
                    severity={snackbar.severity}
                    sx={{ width: '100%' }}
                >
                    {snackbar.message}
                </Alert>
            </Snackbar>
        </Box>
    );
};

export default TreatmentPlanPage;