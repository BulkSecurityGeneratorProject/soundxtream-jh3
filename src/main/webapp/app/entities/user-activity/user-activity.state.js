(function() {
    'use strict';

    angular
        .module('soundxtream3App')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('user-activity', {
            parent: 'entity',
            url: '/user-activity',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'soundxtream3App.user_activity.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/user-activity/user-activities.html',
                    controller: 'User_activityController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('user_activity');
                    $translatePartialLoader.addPart('typeActivity');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('user-activity-detail', {
            parent: 'entity',
            url: '/user-activity/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'soundxtream3App.user_activity.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/user-activity/user-activity-detail.html',
                    controller: 'User_activityDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('user_activity');
                    $translatePartialLoader.addPart('typeActivity');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'User_activity', function($stateParams, User_activity) {
                    return User_activity.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('user-activity.new', {
            parent: 'user-activity',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/user-activity/user-activity-dialog.html',
                    controller: 'User_activityDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                type: null,
                                liked: null,
                                shared: null,
                                dateLiked: null,
                                sharedDate: null,
                                uploadDate: null,
                                createdDate: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('user-activity', null, { reload: true });
                }, function() {
                    $state.go('user-activity');
                });
            }]
        })
        .state('user-activity.edit', {
            parent: 'user-activity',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/user-activity/user-activity-dialog.html',
                    controller: 'User_activityDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['User_activity', function(User_activity) {
                            return User_activity.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('user-activity', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('user-activity.delete', {
            parent: 'user-activity',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/user-activity/user-activity-delete-dialog.html',
                    controller: 'User_activityDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['User_activity', function(User_activity) {
                            return User_activity.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('user-activity', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
