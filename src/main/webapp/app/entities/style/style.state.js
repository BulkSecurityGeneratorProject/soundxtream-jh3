(function() {
    'use strict';

    angular
        .module('soundxtream3App')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('style', {
            parent: 'entity',
            url: '/style',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'soundxtream3App.style.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/style/styles.html',
                    controller: 'StyleController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('style');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('style-detail', {
            parent: 'entity',
            url: '/style/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'soundxtream3App.style.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/style/style-detail.html',
                    controller: 'StyleDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('style');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Style', function($stateParams, Style) {
                    return Style.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('style.new', {
            parent: 'style',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/style/style-dialog.html',
                    controller: 'StyleDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                image: null,
                                imageContentType: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('style', null, { reload: true });
                }, function() {
                    $state.go('style');
                });
            }]
        })
        .state('style.edit', {
            parent: 'style',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/style/style-dialog.html',
                    controller: 'StyleDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Style', function(Style) {
                            return Style.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('style', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('style.delete', {
            parent: 'style',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/style/style-delete-dialog.html',
                    controller: 'StyleDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Style', function(Style) {
                            return Style.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('style', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
