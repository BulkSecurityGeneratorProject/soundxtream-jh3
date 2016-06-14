(function() {
    'use strict';

    angular
        .module('soundxtream3App')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('seguimiento', {
            parent: 'entity',
            url: '/seguimiento',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'soundxtream3App.seguimiento.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/seguimiento/seguimientos.html',
                    controller: 'SeguimientoController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('seguimiento');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('seguimiento-detail', {
            parent: 'entity',
            url: '/seguimiento/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'soundxtream3App.seguimiento.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/seguimiento/seguimiento-detail.html',
                    controller: 'SeguimientoDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('seguimiento');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Seguimiento', function($stateParams, Seguimiento) {
                    return Seguimiento.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('seguimiento.new', {
            parent: 'seguimiento',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/seguimiento/seguimiento-dialog.html',
                    controller: 'SeguimientoDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                followingDate: null,
                                following: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('seguimiento', null, { reload: true });
                }, function() {
                    $state.go('seguimiento');
                });
            }]
        })
        .state('seguimiento.edit', {
            parent: 'seguimiento',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/seguimiento/seguimiento-dialog.html',
                    controller: 'SeguimientoDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Seguimiento', function(Seguimiento) {
                            return Seguimiento.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('seguimiento', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('seguimiento.delete', {
            parent: 'seguimiento',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/seguimiento/seguimiento-delete-dialog.html',
                    controller: 'SeguimientoDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Seguimiento', function(Seguimiento) {
                            return Seguimiento.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('seguimiento', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
