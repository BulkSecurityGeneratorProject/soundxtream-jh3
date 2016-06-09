(function() {
    'use strict';

    angular
        .module('soundxtream3App')
        .controller('TrackDetailController', TrackDetailController);

    TrackDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'DataUtils', 'entity', 'Track', 'User', 'Style'];

    function TrackDetailController($scope, $rootScope, $stateParams, DataUtils, entity, Track, User, Style) {
        var vm = this;

        vm.track = entity;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;

        var unsubscribe = $rootScope.$on('soundxtream3App:trackUpdate', function(event, result) {
            vm.track = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
