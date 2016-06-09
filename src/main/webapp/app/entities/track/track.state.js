(function() {
    'use strict';

    angular
        .module('soundxtream3App')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('track', {
            parent: 'entity',
            url: '/track',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'soundxtream3App.track.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/track/tracks.html',
                    controller: 'TrackController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('track');
                    $translatePartialLoader.addPart('typeTrack');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('track-detail', {
            parent: 'entity',
            url: '/track/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'soundxtream3App.track.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/track/track-detail.html',
                    controller: 'TrackDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('track');
                    $translatePartialLoader.addPart('typeTrack');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Track', function($stateParams, Track) {
                    return Track.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('track.new', {
            parent: 'track',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/track/track-dialog.html',
                    controller: 'TrackDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                label: null,
                                buy_url: null,
                                tags: null,
                                date_upload: null,
                                description: null,
                                location_track: null,
                                type: null,
                                accessUrl: null,
                                artwork: null,
                                artworkContentType: null,
                                visual: null,
                                visualContentType: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('track', null, { reload: true });
                }, function() {
                    $state.go('track');
                });
            }]
        })
        .state('track.edit', {
            parent: 'track',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/track/track-dialog.html',
                    controller: 'TrackDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Track', function(Track) {
                            return Track.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('track', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('track.delete', {
            parent: 'track',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/track/track-delete-dialog.html',
                    controller: 'TrackDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Track', function(Track) {
                            return Track.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('track', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
