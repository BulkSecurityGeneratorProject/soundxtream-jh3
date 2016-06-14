'use strict';

describe('Controller Tests', function() {

    describe('Playlist Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPlaylist, MockTrack, MockUser;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPlaylist = jasmine.createSpy('MockPlaylist');
            MockTrack = jasmine.createSpy('MockTrack');
            MockUser = jasmine.createSpy('MockUser');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Playlist': MockPlaylist,
                'Track': MockTrack,
                'User': MockUser
            };
            createController = function() {
                $injector.get('$controller')("PlaylistDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'soundxtream3App:playlistUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
