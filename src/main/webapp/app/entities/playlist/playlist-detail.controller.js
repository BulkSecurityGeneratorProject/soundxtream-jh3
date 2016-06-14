(function() {
    'use strict';

    angular
        .module('soundxtream3App')
        .controller('PlaylistDetailController', PlaylistDetailController);

    PlaylistDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Playlist', 'Track', 'User'];

    function PlaylistDetailController($scope, $rootScope, $stateParams, entity, Playlist, Track, User) {
        var vm = this;

        vm.playlist = entity;

        var unsubscribe = $rootScope.$on('soundxtream3App:playlistUpdate', function(event, result) {
            vm.playlist = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
