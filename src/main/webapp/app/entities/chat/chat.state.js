(function() {
    'use strict';

    angular
        .module('soundxtream3App')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('chat', {
            parent: 'entity',
            url: '/chat',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'soundxtream3App.chat.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/chat/chats.html',
                    controller: 'ChatController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('chat');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('chat-detail', {
            parent: 'entity',
            url: '/chat/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'soundxtream3App.chat.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/chat/chat-detail.html',
                    controller: 'ChatDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('chat');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Chat', function($stateParams, Chat) {
                    return Chat.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('chat.new', {
            parent: 'chat',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/chat/chat-dialog.html',
                    controller: 'ChatDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('chat', null, { reload: true });
                }, function() {
                    $state.go('chat');
                });
            }]
        })
        .state('chat.edit', {
            parent: 'chat',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/chat/chat-dialog.html',
                    controller: 'ChatDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Chat', function(Chat) {
                            return Chat.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('chat', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('chat.delete', {
            parent: 'chat',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/chat/chat-delete-dialog.html',
                    controller: 'ChatDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Chat', function(Chat) {
                            return Chat.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('chat', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
