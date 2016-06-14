(function() {
    'use strict';

    angular
        .module('soundxtream3App')
        .controller('PlaylistDeleteController',PlaylistDeleteController);

    PlaylistDeleteController.$inject = ['$uibModalInstance', 'entity', 'Playlist'];

    function PlaylistDeleteController($uibModalInstance, entity, Playlist) {
        var vm = this;

        vm.playlist = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Playlist.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
