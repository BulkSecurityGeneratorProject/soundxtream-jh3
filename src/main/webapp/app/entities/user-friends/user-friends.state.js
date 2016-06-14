(function() {
    'use strict';

    angular
        .module('soundxtream3App')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('user-friends', {
            parent: 'entity',
            url: '/user-friends',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'soundxtream3App.user_friends.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/user-friends/user-friends.html',
                    controller: 'User_friendsController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('user_friends');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('user-friends-detail', {
            parent: 'entity',
            url: '/user-friends/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'soundxtream3App.user_friends.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/user-friends/user-friends-detail.html',
                    controller: 'User_friendsDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('user_friends');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'User_friends', function($stateParams, User_friends) {
                    return User_friends.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('user-friends.new', {
            parent: 'user-friends',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/user-friends/user-friends-dialog.html',
                    controller: 'User_friendsDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                friend: null,
                                friendshipDate: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('user-friends', null, { reload: true });
                }, function() {
                    $state.go('user-friends');
                });
            }]
        })
        .state('user-friends.edit', {
            parent: 'user-friends',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/user-friends/user-friends-dialog.html',
                    controller: 'User_friendsDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['User_friends', function(User_friends) {
                            return User_friends.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('user-friends', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('user-friends.delete', {
            parent: 'user-friends',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/user-friends/user-friends-delete-dialog.html',
                    controller: 'User_friendsDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['User_friends', function(User_friends) {
                            return User_friends.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('user-friends', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
