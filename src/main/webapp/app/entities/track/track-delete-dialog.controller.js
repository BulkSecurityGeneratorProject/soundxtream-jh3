(function() {
    'use strict';

    angular
        .module('soundxtream3App')
        .controller('TrackDeleteController',TrackDeleteController);

    TrackDeleteController.$inject = ['$uibModalInstance', 'entity', 'Track'];

    function TrackDeleteController($uibModalInstance, entity, Track) {
        var vm = this;

        vm.track = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Track.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
